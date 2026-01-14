package com.device.management.service;


import com.device.management.dto.*;
import com.device.management.entity.Device;
import com.device.management.entity.DeviceIp;
import com.device.management.entity.Monitor;
import com.device.management.entity.User;
import com.device.management.repository.DeviceIpRepository;
import com.device.management.repository.DeviceRepository;
import com.device.management.repository.MonitorRepository;
import com.device.management.repository.SamplingCheckRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;



/**
 * デバイスサービスクラス
 * デバイス関連のビジネスロジックを処理
 */
@Slf4j
@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private DeviceIpRepository deviceIpRepository;

    @Autowired
    private SamplingCheckRepository samplingCheckRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Transactional
    public DeviceFullDTO insertDevice(DeviceFullDTO deviceFullDTO) {

        Device device = convertToDeviceEntity(deviceFullDTO);

        // insert device
        Device savedDevice = saveOrUpdateDevice(device, false);

        // insert monitors
        List<Monitor> monitors = processMonitors(deviceFullDTO.getMonitors(), savedDevice.getDeviceId(),
                deviceFullDTO.getCreater(), deviceFullDTO.getUpdater(), false);

        // insert DeviceIps
        List<DeviceIp> ips = processDeviceIps(deviceFullDTO.getDeviceIps(), savedDevice.getDeviceId(),
                deviceFullDTO.getCreater(), deviceFullDTO.getUpdater(), false);

        return buildDeviceFullDTO(savedDevice, monitors, ips, deviceFullDTO);
    }

    @Transactional
    public DeviceFullDTO updateDeviceById(String deviceId, DeviceFullDTO deviceFullDTO) {
        deviceFullDTO.setDeviceId(deviceId);

        Device device = convertToDeviceEntity(deviceFullDTO);

        // update device
        Device savedDevice = saveOrUpdateDevice(device, true);

        // update monitors
        List<Monitor> monitors = processMonitors(deviceFullDTO.getMonitors(), savedDevice.getDeviceId(),
                deviceFullDTO.getCreater(), deviceFullDTO.getUpdater(), true);

        // update DeviceIps
        List<DeviceIp> ips = processDeviceIps(deviceFullDTO.getDeviceIps(), savedDevice.getDeviceId(),
                deviceFullDTO.getCreater(), deviceFullDTO.getUpdater(), true);

        return buildDeviceFullDTO(savedDevice, monitors, ips, deviceFullDTO);
    }

    private Device saveOrUpdateDevice(Device device, boolean isUpdate) {
        if (!isUpdate) {

            List<Device> deviceList = deviceRepository.findByDeviceId(device.getDeviceId());
            if (!CollectionUtils.isEmpty(deviceList)) {
                throw new IllegalStateException("device: " + device.getDeviceId() + " exist！");
            }
            device.setCreateTime(LocalDateTime.now());
        } else {

            Device existingDevice = deviceRepository.findById(device.getDeviceId())
                    .orElseThrow(() -> new IllegalArgumentException("device:" + device.getDeviceId() + "not exist."));
            device.setCreateTime(existingDevice.getCreateTime());
        }
        device.setUpdateTime(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    private List<Monitor> processMonitors(List<Monitor> monitors, String deviceId, String creater, String updater, boolean isUpdate) {
        List<Monitor> monitorReturns = new ArrayList<>();
        if (CollectionUtils.isEmpty(monitors)) {
            if (isUpdate) {
                // 更新時に既存のモニタを空にする
                List<Monitor> existingMonitors = monitorRepository.findByDeviceId(deviceId);
                if (!CollectionUtils.isEmpty(existingMonitors)) {
                    monitorRepository.deleteAll(existingMonitors);
                }
            }
            return monitorReturns;
        }

        for (Monitor monitor : monitors) {
            monitor.setDeviceId(deviceId);
            monitor.setCreater(creater);
            monitor.setUpdater(updater);

            if (monitorRepository.existsByMonitorName(monitor.getMonitorName())) {
                Monitor existingMonitor = monitorRepository.findByMonitorName(monitor.getMonitorName());
                if (!existingMonitor.getDeviceId().equals(deviceId)) {
                    throw new IllegalStateException("Monitor " + monitor.getMonitorName() + " is used by " + existingMonitor.getDeviceId() + "!");
                }
                // DB中データがある update
                if (isUpdate) {
                    existingMonitor.setMonitorName(monitor.getMonitorName());
                    existingMonitor.setUpdater(updater);
                    existingMonitor.setUpdateTime(LocalDateTime.now());
                    monitorReturns.add(monitorRepository.save(existingMonitor));
                } else { // DB中データがある insert
                    // insert時に異常がスローされます「設備はすでに存在します」
                }
            } else { // DB中データがない
                // insert または update
                if (isUpdate) {
                    if (monitor.getMonitorId() == null) {
                        monitor.setCreater(updater); // insert
                    } else { // update
                        // 設定しました
                    }
                }
                monitor.setCreateTime(LocalDateTime.now());
                monitor.setUpdateTime(LocalDateTime.now());
                monitorReturns.add(monitorRepository.save(monitor));
            }
        }

        // update時 新しいリストに含まれていない古いレコードを削除する
        if (isUpdate) {
            List<Monitor> existingMonitors = monitorRepository.findByDeviceId(deviceId);
            Set<String> newMonitorNames = monitors.stream()
                    .map(Monitor::getMonitorName)
                    .collect(Collectors.toSet());
            for (Monitor existing : existingMonitors) {
                if (!newMonitorNames.contains(existing.getMonitorName())) {
                    monitorRepository.delete(existing);
                }
            }
        }

        return monitorReturns;
    }

    private List<DeviceIp> processDeviceIps(List<DeviceIp> ipAddresses, String deviceId, String creater, String updater, boolean isUpdate) {
        List<DeviceIp> ipReturns = new ArrayList<>();
        if (CollectionUtils.isEmpty(ipAddresses)) {
            if (isUpdate) {
                // 更新時に既存のip を空にする
                List<DeviceIp> existingIps = deviceIpRepository.findByDeviceId(deviceId);
                if (!CollectionUtils.isEmpty(existingIps)) {
                    deviceIpRepository.deleteAll(existingIps);
                }
            }
            return ipReturns;
        }

        for (DeviceIp deviceIp : ipAddresses) {
            deviceIp.setDeviceId(deviceId);
            deviceIp.setCreater(creater);
            deviceIp.setUpdater(updater);

            // ip チェック
            String ipAddress = deviceIp.getIpAddress().trim();
            try {
                InetAddress.getByName(ipAddress);
            } catch (Exception e) {
                throw new IllegalArgumentException("IPアドレスの形式が無効です: " + ipAddress);
            }

            if (deviceIpRepository.existsByIpAddress(deviceIp.getIpAddress())) {
                DeviceIp existingIp = deviceIpRepository.findByIpAddress(deviceIp.getIpAddress());

                if (!existingIp.getDeviceId().equals(deviceId)) {
                    throw new IllegalStateException("IP " + deviceIp.getIpAddress() + " is used by " + existingIp.getDeviceId() + "!");
                }
                // update
                if (isUpdate) {
                    existingIp.setIpAddress(deviceIp.getIpAddress());
                    existingIp.setUpdater(updater);
                    existingIp.setUpdateTime(LocalDateTime.now());
                    ipReturns.add(deviceIpRepository.save(existingIp));
                } else {
                    // // insert時に異常がスローされます「設備はすでに存在します」
                }
            } else {
                // insert または update
                if (isUpdate) {
                    if (deviceIp.getIpId() == null) {
                        deviceIp.setCreater(updater); // insert
                    } else { // update
                        // 設定しました
                    }
                }
                deviceIp.setCreateTime(LocalDateTime.now());
                deviceIp.setUpdateTime(LocalDateTime.now());
                ipReturns.add(deviceIpRepository.save(deviceIp));
            }
        }

        // update時 新しいリストに含まれていない古いレコードを削除する
        if (isUpdate) {
            List<DeviceIp> existingIps = deviceIpRepository.findByDeviceId(deviceId);
            Set<String> newIpAddresses = ipAddresses.stream()
                    .map(DeviceIp::getIpAddress)
                    .collect(Collectors.toSet());
            for (DeviceIp existing : existingIps) {
                if (!newIpAddresses.contains(existing.getIpAddress())) {
                    deviceIpRepository.delete(existing);
                }
            }
        }

        return ipReturns;
    }

    // DeviceFullDTO で設備パラメータをDeviceに渡す
    private Device convertToDeviceEntity(DeviceFullDTO deviceFullDTO) {
        Device device = new Device();
        device.setDeviceId(deviceFullDTO.getDeviceId());
        device.setDeviceModel(deviceFullDTO.getDeviceModel());
        device.setComputerName(deviceFullDTO.getComputerName());
        device.setLoginUsername(deviceFullDTO.getLoginUsername());
        device.setProject(deviceFullDTO.getProject());
        device.setDevRoom(deviceFullDTO.getDevRoom());
        device.setUserId(deviceFullDTO.getUserId());
        device.setRemark(deviceFullDTO.getRemark());
        device.setSelfConfirmId(deviceFullDTO.getSelfConfirmId());
        device.setOsId(deviceFullDTO.getOsId());
        device.setMemoryId(deviceFullDTO.getMemoryId());
        device.setSsdId(deviceFullDTO.getSsdId());
        device.setHddId(deviceFullDTO.getHddId());
        device.setCreater(deviceFullDTO.getCreater());
        device.setUpdater(deviceFullDTO.getUpdater());
        return device;
    }

    // return DeviceFullDTO
    private DeviceFullDTO buildDeviceFullDTO(Device device, List<Monitor> monitors, List<DeviceIp> ips, DeviceFullDTO originalDTO) {
        DeviceFullDTO dto = new DeviceFullDTO();
        dto.setDeviceId(device.getDeviceId());
        dto.setDeviceModel(device.getDeviceModel());
        dto.setComputerName(device.getComputerName());
        dto.setLoginUsername(device.getLoginUsername());
        dto.setProject(device.getProject());
        dto.setDevRoom(device.getDevRoom());
        dto.setUserId(device.getUserId());
        dto.setRemark(device.getRemark());
        dto.setSelfConfirmId(device.getSelfConfirmId());
        dto.setOsId(device.getOsId());
        dto.setMemoryId(device.getMemoryId());
        dto.setSsdId(device.getSsdId());
        dto.setHddId(device.getHddId());
        dto.setCreater(device.getCreater());
        dto.setUpdater(device.getUpdater());
        dto.setMonitors(monitors);
        dto.setDeviceIps(ips);
        dto.setName(originalDTO.getName());
        dto.setDeptId(originalDTO.getDeptId());
        return dto;
    }

    /**
     * デバイス削除
     * @param deviceId デバイスID
     */
    public void deleteDevice(String deviceId) {
        log.info("Delete device with id: {}", deviceId);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found, DeviceId: " + deviceId));

        samplingCheckRepository.deleteByDeviceId(deviceId);

        deviceRepository.delete(device);

        log.info("Device deleted successfully: {}", deviceId);
    }


    /**
     * デバイス情報をExcel形式でエクスポート
     * @param response HTTPレスポンス
     */
    public void exportDevicesToExcel(HttpServletResponse response) {

        List<Device> devices = deviceRepository.findAll();
        log.info("Exporting {} devices to Excel", devices.size());

        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String fileName = "devices_export_" + timestamp + ".xlsx";

        // ファイル名をURLエンコードして、中文の文字化けなどの問題を防ぐ
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.warn("文件名编码失败，使用原始文件名", e);
        }

        // レスポンスヘッダーを設定する
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        // try-with-resourcesを使用してワークブックが閉じられることを確認する
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("设备清单");

            // ヘッダーを作成する
            createExcelHeader(sheet);

            // データを入力する
            if (!devices.isEmpty()) {
                fillExcelData(sheet, devices);
            } else {
                Row row = sheet.createRow(1);
                row.createCell(0).setCellValue("暂无数据");
            }

            // 列の幅を自動調整
            autoSizeColumns(sheet);

            // レスポンスストリームに書き込む
            workbook.write(response.getOutputStream());

            log.info("Excel导出成功，共导出 {} 条记录", devices.size());
        } catch (IOException e) {
            log.error("导出 Excel 失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }

    /**
     * Excelヘッダーを作成
     * @param sheet シートオブジェクト
     */
    private void createExcelHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "工号", "姓名", "部门",
                "主机设备编号", "显示器设备编号","显示器设备名", "主机型号", "电脑名",
                "IP　地址", "操作系统", "内存单位", "固态硬盘",
                "机械硬盘", "登录用户名", "所在项目", "所在开发室","备注","本人确认"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    /**
     * Excelデータを埋め込む
     * @param sheet シートオブジェクト
     * @param devices デバイスリスト
     */
    private void fillExcelData(Sheet sheet, List<Device> devices) {
        int rowNum = 1;
        for (Device device : devices) {
            Row row = sheet.createRow(rowNum++);

            User user = device.getUser();
            safeSetCellValue(row, 0, user != null ? user.getUserId() : "");
            safeSetCellValue(row, 1, user != null ? user.getName() : "");
            safeSetCellValue(row, 2, user != null ? user.getDeptId() : "");
            // 安全地填充每个单元格
            safeSetCellValue(row, 3, device.getDeviceId());
            safeSetCellValue(row, 4, getAllMonitorIds(device));
            safeSetCellValue(row, 5, getAllMonitorNames(device));
            safeSetCellValue(row, 6, device.getDeviceModel());
            safeSetCellValue(row, 7, device.getComputerName());
            safeSetCellValue(row, 8, getAllIpAddresses(device));
            safeSetCellValue(row, 9, getDictItemName(device.getOsDict()));
            safeSetCellValue(row, 10, getDictItemName(device.getMemoryDict()));
            safeSetCellValue(row, 11, getDictItemName(device.getSsdDict()));
            safeSetCellValue(row, 12, getDictItemName(device.getHddDict()));
            safeSetCellValue(row, 13, device.getLoginUsername());
            safeSetCellValue(row, 14, device.getProject());
            safeSetCellValue(row, 15, device.getDevRoom());
            safeSetCellValue(row, 16, device.getRemark());
            safeSetCellValue(row, 17, getDictItemName(device.getSelfConfirmDict()));
        }
    }

    /**
     * セルに安全に値を設定
     * @param row 行オブジェクト
     * @param cellNum セル番号
     * @param value 設定値
     */
    private void safeSetCellValue(Row row, int cellNum, String value) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value != null ? value : "");
    }

    /**
     * すべてのモニター名を取得（カンマ区切り）
     * @param device デバイスエンティティ
     * @return モニター名文字列
     */
    private String getAllMonitorNames(Device device) {
        if (device.getMonitorInfos() != null && !device.getMonitorInfos().isEmpty()) {
            StringBuilder monitorNames = new StringBuilder();
            for (Monitor monitor : device.getMonitorInfos()) {
                if (monitor.getMonitorName() != null && !monitor.getMonitorName().isEmpty()) {
                    if (monitorNames.length() > 0) {
                        monitorNames.append(", ");
                    }
                    monitorNames.append(monitor.getMonitorName());
                }
            }
            return monitorNames.toString();
        }
        return "";
    }

    /**
     * すべてのモニターIDを取得（カンマ区切り）
     * @param device デバイスエンティティ
     * @return モニターID文字列
     */
    private String getAllMonitorIds(Device device) {
        if (device.getMonitorInfos() != null && !device.getMonitorInfos().isEmpty()) {
            StringBuilder monitorIds = new StringBuilder();
            for (Monitor monitor : device.getMonitorInfos()) {
                if (monitor.getMonitorId() != null) {
                    if (monitorIds.length() > 0) {
                        monitorIds.append(", ");
                    }
                    monitorIds.append(monitor.getMonitorId());
                }
            }
            return monitorIds.toString();
        }
        return "";
    }

    /**
     * すべてのIPアドレスを取得（カンマ区切り）
     * @param device デバイスエンティティ
     * @return IPアドレス文字列
     */
    private String getAllIpAddresses(Device device) {
        if (device.getDeviceIps() != null && !device.getDeviceIps().isEmpty()) {
            StringBuilder ipAddresses = new StringBuilder();
            for (DeviceIp ip : device.getDeviceIps()) {
                if (ip.getIpAddress() != null && !ip.getIpAddress().isEmpty()) {
                    if (ipAddresses.length() > 0) {
                        ipAddresses.append(", ");
                    }
                    ipAddresses.append(ip.getIpAddress());
                }
            }
            return ipAddresses.toString();
        }
        return "";
    }

    /**
     * 辞書項目名を取得
     * @param dict 辞書エンティティ
     * @return 辞書項目名
     */
    private String getDictItemName(com.device.management.entity.Dict dict) {
        return dict != null && dict.getDictItemName() != null ?
                dict.getDictItemName() : "";
    }

    /**
     * 列幅を自動調整
     * @param sheet シートオブジェクト
     */
    private void autoSizeColumns(Sheet sheet) {
        if (sheet.getRow(0) != null) {
            int columnCount = sheet.getRow(0).getLastCellNum();
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
                // 防止列宽过大
                int maxWidth = 50 * 256; // 50个字符宽度
                if (sheet.getColumnWidth(i) > maxWidth) {
                    sheet.setColumnWidth(i, maxWidth);
                }
            }
        }
    }

    /**
     * デバイス一覧取得（ページングとフィルタリング対応）
     */
    public Page<DeviceFullDTO> list(String deviceName, String userId, String name, String project, String devRoom, int page, int size) {
        // ページ番号調整：1始まりから0始まりに変換
        page = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("deviceId").ascending());

        // 条件に合致するデバイス一覧を取得
        List<Device> devices = deviceRepository.findByConditions(deviceName, userId, name, project, devRoom);

        // 総レコード数を取得
        Long totalCount = deviceRepository.countByConditions(deviceName, userId, name, project, devRoom);

        if (devices.isEmpty()) {
            return Page.empty(pageable);
        }

        // ページング処理
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), devices.size());
        List<Device> pagedDevices = devices.subList(start, end);

        // 関連データを一括ロード
        List<String> deviceIds = pagedDevices.stream()
                .map(d -> d.getDeviceId().trim())
                .collect(Collectors.toList());

        Map<String, List<DeviceIp>> ipMap = getDeviceIpMap(deviceIds);
        Map<String, List<Monitor>> monitorMap = getDeviceMonitorMap(deviceIds);

        // DTOに変換
        List<DeviceFullDTO> dtoList = pagedDevices.stream()
                .map(device -> toFullDTOWithRelations(device, ipMap, monitorMap))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, totalCount);
    }

    /**
     * デバイス詳細情報取得
     */
    public DeviceFullDTO detail(String deviceId) {
        if (!StringUtils.hasText(deviceId)) return null;

        try {
            Device device = deviceRepository.findByDeviceIdWithDicts(deviceId.trim());
            if (device == null) {
                log.error("デバイス詳細が見つかりません: {}", deviceId);
                return null;
            }

            List<String> ids = Collections.singletonList(device.getDeviceId().trim());
            return toFullDTOWithRelations(device, getDeviceIpMap(ids), getDeviceMonitorMap(ids));
        } catch (Exception e) {
            log.error("デバイス詳細クエリ中にエラーが発生しました: {}", deviceId, e);
            return null;
        }
    }

    /**
     * 単一ユーザーの全デバイスを取得
     */
    public List<DeviceFullDTO> getDevicesByUserId(String userId) {
        if (!StringUtils.hasText(userId)) return new ArrayList<>();
        Map<String, List<DeviceFullDTO>> resultMap = getDeviceMapByUserIds(Collections.singletonList(userId.trim()));
        return resultMap.getOrDefault(userId.trim(), new ArrayList<>());
    }

    /**
     * バッチ処理：ユーザーIDリストに対応するデバイスマッピングを取得
     */
    public Map<String, List<DeviceFullDTO>> getDeviceMapByUserIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();

        List<Device> allDevices = deviceRepository.findByUserIdsWithDicts(userIds);
        if (allDevices.isEmpty()) return Collections.emptyMap();

        List<String> deviceIds = allDevices.stream()
                .map(d -> d.getDeviceId().trim())
                .collect(Collectors.toList());

        Map<String, List<DeviceIp>> ipMap = getDeviceIpMap(deviceIds);
        Map<String, List<Monitor>> monitorMap = getDeviceMonitorMap(deviceIds);

        return allDevices.stream()
                .map(dev -> toFullDTOWithRelations(dev, ipMap, monitorMap))
                .collect(Collectors.groupingBy(DeviceFullDTO::getUserId));
    }

    /**
     * 全ての開発室名を取得（重複排除）
     */
    public List<String> getAllDevRooms() {
        try {
            List<String> devRooms = deviceRepository.findDistinctDevRooms();

            // 空値をフィルタリングし、前後の空白をトリム
            return devRooms.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("開発室一覧取得中にエラーが発生しました", e);
            return Collections.emptyList();
        }
    }

    /**
     * 全てのプロジェクト名を取得（重複排除）
     */
    public List<String> getAllProjects() {
        try {
            List<String> projects = deviceRepository.findDistinctProjects();

            // 空値をフィルタリングし、前後の空白をトリム
            return projects.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("プロジェクト一覧取得中にエラーが発生しました", e);
            return Collections.emptyList();
        }
    }

    /**
     * UserService呼び出し用：デバイス名またはユーザーIDでフィルタリングしてユニークなユーザーIDリストを取得
     */
    public List<String> findUserIdsByCondition(String deviceName, String userId) {
        return deviceRepository.findUserIdsByCondition(deviceName, userId);
    }

    /**
     * デバイスエンティティを関連データを含むDTOに変換（DeviceFullDTO用）
     */
    private DeviceFullDTO toFullDTOWithRelations(Device device, Map<String, List<DeviceIp>> ipMap, Map<String, List<Monitor>> monitorMap) {
        DeviceFullDTO dto = toFullBasicDTO(device);
        String key = device.getDeviceId().trim();

        // IPリストを設定（直接使用DeviceIp实体）
        List<DeviceIp> ips = ipMap.getOrDefault(key, new ArrayList<>());
        dto.setDeviceIps(ips);

        // モニターリストを設定（直接使用Monitor实体）
        List<Monitor> monitors = monitorMap.getOrDefault(key, new ArrayList<>());
        dto.setMonitors(monitors);

        return dto;
    }

    /**
     * デバイスエンティティを基本情報のみのDTOに変換（DeviceFullDTO用）
     */
    private DeviceFullDTO toFullBasicDTO(Device device) {
        DeviceFullDTO dto = DeviceFullDTO.builder()
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
                .monitors(new ArrayList<>())
                .deviceIps(new ArrayList<>())
                .build();

        return dto;
    }

    /**
     * ヘルパーメソッド：デバイスIDリストに対応するIP情報を取得
     */
    private Map<String, List<DeviceIp>> getDeviceIpMap(List<String> deviceIds) {
        if (deviceIds.isEmpty()) return Collections.emptyMap();

        List<Object[]> results = deviceRepository.findDeviceIpsByDeviceIds(deviceIds);
        Map<String, List<DeviceIp>> map = new HashMap<>();

        for (Object[] row : results) {
            String id = ((String) row[0]).trim();
            map.computeIfAbsent(id, k -> new ArrayList<>()).add((DeviceIp) row[1]);
        }
        return map;
    }

    /**
     * ヘルパーメソッド：デバイスIDリストに対応するモニター情報を取得
     */
    private Map<String, List<Monitor>> getDeviceMonitorMap(List<String> deviceIds) {
        if (deviceIds.isEmpty()) return Collections.emptyMap();

        List<Object[]> results = deviceRepository.findMonitorsByDeviceIds(deviceIds);
        Map<String, List<Monitor>> map = new HashMap<>();

        for (Object[] row : results) {
            String id = ((String) row[0]).trim();
            map.computeIfAbsent(id, k -> new ArrayList<>()).add((Monitor) row[1]);
        }
        return map;
    }

}