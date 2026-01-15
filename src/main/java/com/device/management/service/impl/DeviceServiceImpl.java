package com.device.management.service.impl;

import com.device.management.dto.*;
import com.device.management.entity.*;
import com.device.management.exception.*;
import com.device.management.repository.*;
import com.device.management.service.DeviceService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.commons.validator.routines.InetAddressValidator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private MonitorRepository monitorRepository;
    @Autowired
    private DeviceIpRepository deviceIpRepository;
    @Autowired
    private SamplingCheckRepository samplingCheckRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /* -------------------- 新增 -------------------- */
    @Override
    @Transactional
    public ApiResponse<DeviceFullDTO> insertDevice(DeviceFullDTO dto) {
        log.info("新增设备: {}", dto.getDeviceId());

        // --- 1. 基础参数校验（400） ---
        if (!StringUtils.hasText(dto.getDeviceId())) {
            throw new ParameterException ("设备ID不能为空");
        }
        if (!StringUtils.hasText(dto.getCreater())) {
            throw new ParameterException ("创建人不能为空");
        }

        // --- 2. 业务冲突检查（409） ---
        if (deviceRepository.existsByDeviceId(dto.getDeviceId())) {
            throw new ResourceConflictException("设备已存在: " + dto.getDeviceId());
        }

        if (!CollectionUtils.isEmpty(dto.getMonitors())) {
            Set<String> names = new HashSet<>();
            for (Monitor m : dto.getMonitors()) {
                if (!StringUtils.hasText(m.getMonitorName())) {
                    throw new ParameterException ("监视器名称不能为空");
                }
                if (names.contains(m.getMonitorName())) {
                    throw new ParameterException ("监视器名称重复: " + m.getMonitorName());
                }
                names.add(m.getMonitorName());
                if (monitorRepository.existsByMonitorName(m.getMonitorName())) {
                    Monitor exist = monitorRepository.findByMonitorName(m.getMonitorName());
                    throw new ResourceConflictException("监视器名称已被其他设备占用: " + m.getMonitorName() + ", 设备ID: " + exist.getDeviceId());
                }
            }
        }

        if (!CollectionUtils.isEmpty(dto.getDeviceIps())) {
            Set<String> ips = new HashSet<>();
            // --- IP 格式校验（400） ---
            for (DeviceIp ip : dto.getDeviceIps()) {
                String addr = ip.getIpAddress().trim();
                validateIpv4(addr);   // ① 先格式
            }
            // --- 重复/占用检查（400/409） ---
            Set<String> ipSet = new HashSet<>();
            for (DeviceIp ip : dto.getDeviceIps()) {
                String addr = ip.getIpAddress().trim();
                if (ipSet.contains(addr)) {
                    throw new ParameterException("IP地址重复: " + addr);
                }
                ipSet.add(addr);
                if (deviceIpRepository.existsByIpAddress(addr)) {
                    DeviceIp exist = deviceIpRepository.findByIpAddress(addr);
                    throw new ResourceConflictException("IP地址已被其他设备占用: " + addr + ", 设备ID: " + exist.getDeviceId());
                }
            }
        }

        // --- 3. 保存 ---
        Device entity = convertToDeviceEntity(dto);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        Device saved = deviceRepository.save(entity);

        List<Monitor> monitors = saveMonitors(dto.getMonitors(), saved.getDeviceId(), dto.getCreater(), dto.getUpdater());
        List<DeviceIp> ips = saveDeviceIps(dto.getDeviceIps(), saved.getDeviceId(), dto.getCreater(), dto.getUpdater());

        DeviceFullDTO result = buildDeviceFullDTO(saved, monitors, ips, dto);
        return ApiResponse.success("设备新增成功", result);
    }

    /* -------------------- 更新 -------------------- */
    @Override
    @Transactional
    public ApiResponse<DeviceFullDTO> updateDeviceById(String deviceId, DeviceFullDTO dto) {
        log.info("更新设备: {}", deviceId);

        if (!StringUtils.hasText(deviceId)) {
            throw new ParameterException ("设备ID不能为空");
        }
        if (dto == null) {
            throw new ParameterException ("请求体不能为空");
        }

        Device existDevice = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("设备不存在: " + deviceId));

        // 监视器冲突
        if (!CollectionUtils.isEmpty(dto.getMonitors())) {
            Set<String> names = new HashSet<>();
            for (Monitor m : dto.getMonitors()) {
                if (!StringUtils.hasText(m.getMonitorName())) {
                    throw new ParameterException ("监视器名称不能为空");
                }
                if (names.contains(m.getMonitorName())) {
                    throw new ParameterException ("监视器名称重复: " + m.getMonitorName());
                }
                names.add(m.getMonitorName());
                if (monitorRepository.existsByMonitorName(m.getMonitorName())) {
                    Monitor exist = monitorRepository.findByMonitorName(m.getMonitorName());
                    if (!exist.getDeviceId().equals(deviceId)) {
                        throw new ResourceConflictException("监视器名称已被其他设备占用: " + m.getMonitorName() + ", 设备ID: " + exist.getDeviceId());
                    }
                }
            }
        }

        // IP冲突
        if (!CollectionUtils.isEmpty(dto.getDeviceIps())) {
            Set<String> ips = new HashSet<>();
            for (DeviceIp ip : dto.getDeviceIps()) {
                String addr = ip.getIpAddress().trim();
                if (ips.contains(addr)) {
                    throw new ParameterException ("IP地址重复: " + addr);
                }
                ips.add(addr);
                try {
                    InetAddress.getByName(addr);
                } catch (Exception e) {
                    throw new InvalidIpAddressException("IP地址格式无效: " + addr);
                }
                if (deviceIpRepository.existsByIpAddress(addr)) {
                    DeviceIp exist = deviceIpRepository.findByIpAddress(addr);
                    if (!exist.getDeviceId().equals(deviceId)) {
                        throw new ResourceConflictException("IP地址已被其他设备占用: " + addr + ", 设备ID: " + exist.getDeviceId());
                    }
                }
            }
        }

        Device entity = convertToDeviceEntity(dto);
        entity.setCreateTime(existDevice.getCreateTime());
        entity.setUpdateTime(LocalDateTime.now());
        Device saved = deviceRepository.save(entity);

        List<Monitor> monitors = updateMonitors(dto.getMonitors(), saved.getDeviceId(), dto.getCreater(), dto.getUpdater());
        List<DeviceIp> ips = updateDeviceIps(dto.getDeviceIps(), saved.getDeviceId(), dto.getCreater(), dto.getUpdater());

        DeviceFullDTO result = buildDeviceFullDTO(saved, monitors, ips, dto);
        return ApiResponse.success("设备更新成功", result);
    }

    /* -------------------- 删除 -------------------- */
    @Override
    @Transactional
    public ApiResponse<String> deleteDevice(String deviceId) {
        log.info("Delete device with id: {}", deviceId);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("设备不存在: " + deviceId));

        samplingCheckRepository.deleteByDeviceId(deviceId);

        List<Monitor> monitors = monitorRepository.findByDeviceId(deviceId);
        if (!CollectionUtils.isEmpty(monitors)) {
            monitorRepository.deleteAll(monitors);
        }

        List<DeviceIp> deviceIps = deviceIpRepository.findByDeviceId(deviceId);
        if (!CollectionUtils.isEmpty(deviceIps)) {
            deviceIpRepository.deleteAll(deviceIps);
        }

        deviceRepository.delete(device);
        return ApiResponse.success("设备删除成功", deviceId);
    }

    /* -------------------- 导出 -------------------- */
    @Override
    @Transactional
    public void exportDevicesToExcel(HttpServletResponse response) {
        log.info("Starting device export to Excel");

        List<Device> devices = deviceRepository.findAll();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String fileName = "devices_export_" + timestamp + ".xlsx";

        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("Export file name cannot be empty");
        }
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (Exception e) {
            throw new AllException("Filename encoding failed: " + e.getMessage());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("设备清单");
            createExcelHeader(sheet);
            if (!devices.isEmpty()) {
                fillExcelData(sheet, devices);
            } else {
                Row row = sheet.createRow(1);
                row.createCell(0).setCellValue("暂无数据");
            }
            autoSizeColumns(sheet);
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new AllException("Excel写入失败: " + e.getMessage());
        }
    }

    /* -------------------- 列表 -------------------- */
    // 将原来的 list 方法完整替换为以下内容：

    @Override
    public Page<DeviceFullDTO> list(String computerName, String userId, int page, int size) {



            // 参数验证
            if (page < 1) {
                throw new AllException(400, "ページ番号は1以上である必要があります");
            }
            if (size < 1 || size > 100) {
                throw new AllException(400, "ページサイズは1から100の間である必要があります");
            }

            // 页面编号调整：从1开始转换为从0开始
            page = page - 1;
            Pageable pageable = PageRequest.of(page, size, Sort.by("deviceId").ascending());

            // ✅ 修改1：将空字符串转为null，让JPA自动忽略该条件
            String searchComputerName = StringUtils.hasText(computerName) ? computerName : null;
            String searchUserId = StringUtils.hasText(userId) ? userId : null;

            // 查询数据
            List<Device> devices = deviceRepository.findByComputerNameContainingIgnoreCaseAndUserIdContainingIgnoreCase(
                    searchComputerName, searchUserId
            );

            // 查询总数
            Long totalCount = deviceRepository.countByComputerNameContainingIgnoreCaseAndUserIdContainingIgnoreCase(
                    searchComputerName, searchUserId
            );

            // ✅ 修改2：移除404抛出逻辑，列表查询始终返回200 OK
            if (devices.isEmpty()) {
                log.info("デバイスが登録されていません、または条件に一致するデバイスがありません: computerName={}, userId={}", computerName, userId);
                return Page.empty(pageable);  // 直接返回空页面，不抛出异常
            }

            // 分页处理
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), devices.size());
            List<Device> pagedDevices = devices.subList(start, end);

            // 批量加载关联数据
            List<String> deviceIds = pagedDevices.stream()
                    .map(d -> d.getDeviceId().trim())
                    .collect(Collectors.toList());

            Map<String, List<DeviceIp>> ipMap = getDeviceIpMap(deviceIds);
            Map<String, List<Monitor>> monitorMap = getDeviceMonitorMap(deviceIds);

            // 转换为DTO
            List<DeviceFullDTO> dtoList = pagedDevices.stream()
                    .map(device -> toFullDTOWithRelations(device, ipMap, monitorMap))
                    .collect(Collectors.toList());

            log.debug("デバイス一覧を取得しました: 件数={}, 総数={}", dtoList.size(), totalCount);
            return new PageImpl<>(dtoList, pageable, totalCount);

    }


    /* -------------------- 详情 -------------------- */
    @Override
    public ApiResponse<DeviceFullDTO> detail(String deviceId) {
        if (!StringUtils.hasText(deviceId)) {
            throw new ParameterException ("设备ID不能为空");
        }

        Device device = deviceRepository.findByDeviceIdWithDicts(deviceId.trim());
        if (device == null) {
            throw new ResourceNotFoundException("设备不存在: " + deviceId.trim());
        }

        List<String> ids = Collections.singletonList(device.getDeviceId().trim());
        DeviceFullDTO dto = toFullDTOWithRelations(device,
                getDeviceIpMap(ids),
                getDeviceMonitorMap(ids));
        return ApiResponse.success("查询成功", dto);
    }

    /* -------------------- 私有工具 -------------------- */
    private Device convertToDeviceEntity(DeviceFullDTO dto) {
        Device d = new Device();
        d.setDeviceId(dto.getDeviceId());
        d.setDeviceModel(dto.getDeviceModel());
        d.setComputerName(dto.getComputerName());
        d.setLoginUsername(dto.getLoginUsername());
        d.setProject(dto.getProject());
        d.setDevRoom(dto.getDevRoom());
        d.setUserId(dto.getUserId());
        d.setRemark(dto.getRemark());
        d.setSelfConfirmId(dto.getSelfConfirmId());
        d.setOsId(dto.getOsId());
        d.setMemoryId(dto.getMemoryId());
        d.setSsdId(dto.getSsdId());
        d.setHddId(dto.getHddId());
        d.setCreater(dto.getCreater());
        d.setUpdater(dto.getUpdater());
        return d;
    }

    private DeviceFullDTO buildDeviceFullDTO(Device d, List<Monitor> monitors, List<DeviceIp> ips, DeviceFullDTO orig) {
        DeviceFullDTO dto = new DeviceFullDTO();
        dto.setDeviceId(d.getDeviceId());
        dto.setDeviceModel(d.getDeviceModel());
        dto.setComputerName(d.getComputerName());
        dto.setLoginUsername(d.getLoginUsername());
        dto.setProject(d.getProject());
        dto.setDevRoom(d.getDevRoom());
        dto.setUserId(d.getUserId());
        dto.setRemark(d.getRemark());
        dto.setSelfConfirmId(d.getSelfConfirmId());
        dto.setOsId(d.getOsId());
        dto.setMemoryId(d.getMemoryId());
        dto.setSsdId(d.getSsdId());
        dto.setHddId(d.getHddId());
        dto.setCreater(d.getCreater());
        dto.setUpdater(d.getUpdater());
        dto.setMonitors(monitors);
        dto.setDeviceIps(ips);
        dto.setName(orig.getName());
        dto.setDeptId(orig.getDeptId());
        return dto;
    }

    private List<Monitor> saveMonitors(List<Monitor> list, String deviceId, String creater, String updater) {
        if (CollectionUtils.isEmpty(list)) return Collections.emptyList();
        return list.stream()
                .peek(m -> {
                    m.setDeviceId(deviceId);
                    m.setCreater(creater);
                    m.setUpdater(updater);
                    m.setCreateTime(LocalDateTime.now());
                    m.setUpdateTime(LocalDateTime.now());
                })
                .map(monitorRepository::save)
                .toList();
    }

    private List<DeviceIp> saveDeviceIps(List<DeviceIp> list, String deviceId, String creater, String updater) {
        if (CollectionUtils.isEmpty(list)) return Collections.emptyList();
        return list.stream()
                .peek(ip -> {
                    ip.setDeviceId(deviceId);
                    ip.setCreater(creater);
                    ip.setUpdater(updater);
                    ip.setCreateTime(LocalDateTime.now());
                    ip.setUpdateTime(LocalDateTime.now());
                })
                .map(deviceIpRepository::save)
                .toList();
    }

    private List<Monitor> updateMonitors(List<Monitor> list, String deviceId, String creater, String updater) {
        List<Monitor> exist = monitorRepository.findByDeviceId(deviceId);
        if (!CollectionUtils.isEmpty(exist)) monitorRepository.deleteAll(exist);
        return saveMonitors(list, deviceId, updater, updater);
    }

    private List<DeviceIp> updateDeviceIps(List<DeviceIp> list, String deviceId, String creater, String updater) {
        List<DeviceIp> exist = deviceIpRepository.findByDeviceId(deviceId);
        if (!CollectionUtils.isEmpty(exist)) deviceIpRepository.deleteAll(exist);
        return saveDeviceIps(list, deviceId, updater, updater);
    }

    private Map<String, List<DeviceIp>> getDeviceIpMap(List<String> deviceIds) {
        if (CollectionUtils.isEmpty(deviceIds)) return Collections.emptyMap();
        List<Object[]> rows = deviceRepository.findDeviceIpsByDeviceIds(deviceIds);
        Map<String, List<DeviceIp>> map = new HashMap<>();
        for (Object[] r : rows) {
            if (r.length >= 2 && r[0] != null && r[1] != null) {
                String id = ((String) r[0]).trim();
                map.computeIfAbsent(id, k -> new ArrayList<>()).add((DeviceIp) r[1]);
            }
        }
        return map;
    }

    private Map<String, List<Monitor>> getDeviceMonitorMap(List<String> deviceIds) {
        if (CollectionUtils.isEmpty(deviceIds)) return Collections.emptyMap();
        List<Object[]> rows = deviceRepository.findMonitorsByDeviceIds(deviceIds);
        Map<String, List<Monitor>> map = new HashMap<>();
        for (Object[] r : rows) {
            if (r.length >= 2 && r[0] != null && r[1] != null) {
                String id = ((String) r[0]).trim();
                map.computeIfAbsent(id, k -> new ArrayList<>()).add((Monitor) r[1]);
            }
        }
        return map;
    }

    private String buildSearchConditionMessage(String deviceName, String userId) {
        List<String> list = new ArrayList<>();
        if (StringUtils.hasText(deviceName)) list.add("デバイス名: " + deviceName);
        if (StringUtils.hasText(userId)) list.add("ユーザーID: " + userId);
        return list.isEmpty() ? "条件なし" : String.join(", ", list);
    }

    /* ---------- Excel 私有方法 ---------- */
    private void createExcelHeader(Sheet sheet) {
        Row row = sheet.createRow(0);
        String[] headers = {"工号", "姓名", "部门", "主机设备编号", "显示器设备编号", "显示器设备名", "主机型号", "电脑名",
                "IP　地址", "操作系统", "内存单位", "固态硬盘", "机械硬盘", "登录用户名", "所在项目", "所在开发室", "备注", "本人确认"};
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }
    }

    private void fillExcelData(Sheet sheet, List<Device> devices) {
        int rowNum = 1;
        for (Device d : devices) {
            Row r = sheet.createRow(rowNum++);
            User u = d.getUser();
            safeSetCellValue(r, 0, u == null ? "" : u.getUserId());
            safeSetCellValue(r, 1, u == null ? "" : u.getName());
            safeSetCellValue(r, 2, u == null ? "" : u.getDeptId());
            safeSetCellValue(r, 3, d.getDeviceId());
            safeSetCellValue(r, 4, getAllMonitorIds(d));
            safeSetCellValue(r, 5, getAllMonitorNames(d));
            safeSetCellValue(r, 6, d.getDeviceModel());
            safeSetCellValue(r, 7, d.getComputerName());
            safeSetCellValue(r, 8, getAllIpAddresses(d));
            safeSetCellValue(r, 9, getDictItemName(d.getOsDict()));
            safeSetCellValue(r, 10, getDictItemName(d.getMemoryDict()));
            safeSetCellValue(r, 11, getDictItemName(d.getSsdDict()));
            safeSetCellValue(r, 12, getDictItemName(d.getHddDict()));
            safeSetCellValue(r, 13, d.getLoginUsername());
            safeSetCellValue(r, 14, d.getProject());
            safeSetCellValue(r, 15, d.getDevRoom());
            safeSetCellValue(r, 16, d.getRemark());
            safeSetCellValue(r, 17, getDictItemName(d.getSelfConfirmDict()));
        }
    }

    private void safeSetCellValue(Row row, int cellNum, String value) {
        row.createCell(cellNum).setCellValue(value == null ? "" : value);
    }

    // 原来错误写法：stream().map(...).collect(...)`
// 修正后：
    private String getAllMonitorNames(Device d) {
        if (CollectionUtils.isEmpty(d.getMonitorInfos())) return "";
        return d.getMonitorInfos().stream()
                .map(Monitor::getMonitorName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private String getAllMonitorIds(Device d) {
        if (CollectionUtils.isEmpty(d.getMonitorInfos())) return "";
        return d.getMonitorInfos().stream()
                .map(m -> String.valueOf(m.getMonitorId()))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private String getAllIpAddresses(Device d) {
        if (CollectionUtils.isEmpty(d.getDeviceIps())) return "";
        return d.getDeviceIps().stream()
                .map(DeviceIp::getIpAddress)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private String getDictItemName(com.device.management.entity.Dict dict) {
        return dict == null || dict.getDictItemName() == null ? "" : dict.getDictItemName();
    }

    private void autoSizeColumns(Sheet sheet) {
        if (sheet.getRow(0) == null) return;
        int count = sheet.getRow(0).getLastCellNum();
        for (int i = 0; i < count; i++) {
            sheet.autoSizeColumn(i);
            int max = 50 * 256;
            if (sheet.getColumnWidth(i) > max) sheet.setColumnWidth(i, max);
        }
    }

    /* ----------  私有工具 ---------- */
    private DeviceFullDTO toFullDTOWithRelations(Device device,
                                                 Map<String, List<DeviceIp>> ipMap,
                                                 Map<String, List<Monitor>> monitorMap) {
        DeviceFullDTO dto = toFullBasicDTO(device);
        String key = device.getDeviceId().trim();
        dto.setDeviceIps(ipMap.getOrDefault(key, List.of()));
        dto.setMonitors(monitorMap.getOrDefault(key, List.of()));
        return dto;
    }

    private DeviceFullDTO toFullBasicDTO(Device device) {
        return DeviceFullDTO.builder()
                .deviceId(device.getDeviceId().trim())
                .userId(device.getUserId())
                .name(device.getUser() != null ? device.getUser().getName() : null)
                .deptId(device.getUser() != null ? device.getUser().getDeptId() : null)
                .deviceModel(device.getDeviceModel())
                .computerName(device.getComputerName())
                .loginUsername(device.getLoginUsername())
                .project(device.getProject())
                .devRoom(device.getDevRoom())
                .remark(device.getRemark())
                .selfConfirmDict(DictDTO.fromEntity(device.getSelfConfirmDict()))
                .osDict(DictDTO.fromEntity(device.getOsDict()))
                .memoryDict(DictDTO.fromEntity(device.getMemoryDict()))
                .ssdDict(DictDTO.fromEntity(device.getSsdDict()))
                .hddDict(DictDTO.fromEntity(device.getHddDict()))
                .createTime(device.getCreateTime())
                .creater(device.getCreater())
                .updateTime(device.getUpdateTime())
                .updater(device.getUpdater())
                .monitors(List.of())
                .deviceIps(List.of())
                .build();
    }




    private static final InetAddressValidator IP_VALIDATOR = InetAddressValidator.getInstance();

    /** 真正的 IPv4 格式校验 */
    private void validateIpv4(String addr) {
        log.info("🔍 开始校验 IP: {}", addr);
        if (!IP_VALIDATOR.isValidInet4Address(addr)) {
            log.warn("❌ IP 格式无效: {}", addr);
            throw new InvalidIpAddressException("IP地址格式无效: " + addr);
        }
        log.info("✅ IP 格式合法: {}", addr);
    }

}