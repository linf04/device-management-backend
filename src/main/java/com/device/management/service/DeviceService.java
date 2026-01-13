package com.device.management.service;


import com.device.management.dto.*;
import com.device.management.entity.Device;
import com.device.management.entity.DeviceIp;
import com.device.management.entity.Monitor;
import com.device.management.entity.User;
import com.device.management.repository.DeviceIpRepository;
import com.device.management.repository.DeviceRepository;
import com.device.management.repository.MonitorRepository;
import com.device.management.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
import org.springframework.util.StringUtils;
import org.springframework.data.domain.*;
import lombok.RequiredArgsConstructor;

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

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Transactional
    public DeviceFullDTO insertDevice(DeviceFullDTO deviceFullDTO){

        DeviceDTO deviceDTO = DeviceDTO.builder()
                .deviceId(deviceFullDTO.getDeviceId()).deviceModel(deviceFullDTO.getDeviceModel())
                .computerName(deviceFullDTO.getComputerName()).loginUsername(deviceFullDTO.getLoginUsername())
                .project(deviceFullDTO.getProject()).devRoom(deviceFullDTO.getDevRoom()).userId(deviceFullDTO.getUserId())
                .remark(deviceFullDTO.getRemark()).selfConfirmId(deviceFullDTO.getSelfConfirmId()).osId(deviceFullDTO.getOsId())
                .memoryId(deviceFullDTO.getMemoryId()).ssdId(deviceFullDTO.getSsdId()).hddId(deviceFullDTO.getHddId())
                .creater(deviceFullDTO.getCreater()).updater(deviceFullDTO.getUpdater())
                .build();


        // device insert
        List<Device> deviceList = deviceRepository.findByDeviceId(deviceDTO.getDeviceId());

        if(!CollectionUtils.isEmpty(deviceList)) { // 設備の存在
            throw new IllegalStateException("device:" + deviceDTO.getDeviceId() + "exist.");
        }

        Device device = convertDeviceToEntity(deviceDTO);
        device.setCreateTime(LocalDateTime.now()); // 作成時刻の設定
        device.setUpdateTime(LocalDateTime.now()); // 更新日時の設定

        Device deviceReturn = deviceRepository.save(device); // 保存された設備に戻る


        // monitor insert
        List<Monitor> monitorReturns = new ArrayList<>();
        if (!CollectionUtils.isEmpty(deviceFullDTO.getMonitors())) {
            for (MonitorDTO monitorDTO : deviceFullDTO.getMonitors()) {

                monitorDTO.setDeviceId(deviceFullDTO.getDeviceId());
                monitorDTO.setCreater(deviceFullDTO.getCreater());
                monitorDTO.setUpdater(deviceFullDTO.getUpdater());

                Monitor monitor = convertMonitorToEntity(monitorDTO);

                if (!monitorRepository.existsByMonitorName(monitorDTO.getMonitorName())) {

                    monitor.setCreateTime(LocalDateTime.now());
                    monitor.setUpdateTime(LocalDateTime.now());

                    Monitor monitorReturn = monitorRepository.save(monitor);
                    monitorReturns.add(monitorReturn);
                } else { // exsit

                    Monitor monitorDB = monitorRepository.findByMonitorName(monitorDTO.getMonitorName());

                    if(!monitorDB.getDeviceId().equals(monitorDTO.getDeviceId())) {
                        //                System.out.println(monitorDB.getMonitorId() + "++++++++++" + monitorDTO.getMonitorId()); // 1  null
                        throw new IllegalStateException("Monitor " + monitorDTO.getMonitorName() + " is used by " + monitorDB.getDeviceId() + "!");
                    }

                    monitorReturns.add(monitorDB);
                }
            }
        }

        // ip insert
        List<DeviceIp> ipReturns = new ArrayList<>();
        if (!CollectionUtils.isEmpty(deviceFullDTO.getIpAddresses())) {
            for (DeviceIpDTO deviceIpDTO : deviceFullDTO.getIpAddresses()) {

                deviceIpDTO.setDeviceId(deviceFullDTO.getDeviceId());
                deviceIpDTO.setCreater(deviceFullDTO.getCreater());
                deviceIpDTO.setUpdater(deviceFullDTO.getUpdater());

                // ipチェック
                String ipAddress = deviceIpDTO.getIpAddress();
                ipAddress = ipAddress.trim();

                try {
                    InetAddress.getByName(ipAddress);
                } catch (Exception e) {
                    throw new IllegalArgumentException("IPアドレスの形式が無効です: " + ipAddress);
                }

                // insert
                if (!deviceIpRepository.existsByIpAddress(deviceIpDTO.getIpAddress())) {

                    DeviceIp deviceIp = convertDeviceIpToEntity(deviceIpDTO);

                    deviceIp.setCreateTime(LocalDateTime.now());
                    deviceIp.setUpdateTime(LocalDateTime.now());

                    DeviceIp deviceIpReturn = deviceIpRepository.save(deviceIp);
                    ipReturns.add(deviceIpReturn);
                } else {

                    DeviceIp deviceIpDB = deviceIpRepository.findByIpAddress(deviceIpDTO.getIpAddress());

                    if(!deviceIpDB.getDeviceId().equals(deviceIpDTO.getDeviceId())) {
                        throw new IllegalStateException("Ip " + deviceIpDTO.getIpAddress() + " is used by " + deviceIpDB.getDeviceId() + "!");
                    }

                    ipReturns.add(deviceIpDB);
                }
            }
        }

        DeviceFullDTO deviceFullDtoReturn = DeviceFullDTO.builder()
                .deviceId(deviceReturn.getDeviceId()).deviceModel(deviceReturn.getDeviceModel())
                .computerName(deviceReturn.getComputerName()).loginUsername(deviceReturn.getLoginUsername())
                .project(deviceReturn.getProject()).devRoom(deviceReturn.getDevRoom()).userId(deviceReturn.getUserId())
                .remark(deviceReturn.getRemark()).selfConfirmId(deviceReturn.getSelfConfirmId()).osId(deviceReturn.getOsId())
                .memoryId(deviceReturn.getMemoryId()).ssdId(deviceReturn.getSsdId()).hddId(deviceReturn.getHddId())
                .creater(deviceReturn.getCreater()).updater(deviceReturn.getUpdater())
                .monitors(convertMonitorsToDTOList(monitorReturns))
                .ipAddresses(convertDeviceIpsToDTOList(ipReturns))
                .name(deviceFullDTO.getName()).deptId(deviceFullDTO.getDeptId())
                .build();

        return deviceFullDtoReturn; //設備FullDTOを返す
    }


    @Transactional
    public DeviceFullDTO updateDeviceById(String deviceId, DeviceFullDTO deviceFullDTO) {

//        System.out.println("!!!!!!!!!!!!" + deviceFullDTO + "!!!!!!!!!!!!");

        deviceFullDTO.setDeviceId(deviceId); // 渡されたパラメータのdeviceIdの後ろに使用されます

        DeviceDTO deviceDTO = DeviceDTO.builder()
                .deviceId(deviceFullDTO.getDeviceId()).deviceModel(deviceFullDTO.getDeviceModel())
                .computerName(deviceFullDTO.getComputerName()).loginUsername(deviceFullDTO.getLoginUsername())
                .project(deviceFullDTO.getProject()).devRoom(deviceFullDTO.getDevRoom()).userId(deviceFullDTO.getUserId())
                .remark(deviceFullDTO.getRemark()).selfConfirmId(deviceFullDTO.getSelfConfirmId()).osId(deviceFullDTO.getOsId())
                .memoryId(deviceFullDTO.getMemoryId()).ssdId(deviceFullDTO.getSsdId()).hddId(deviceFullDTO.getHddId())
                .creater(deviceFullDTO.getCreater()).updater(deviceFullDTO.getUpdater())
                .build();


        // device update
        // 機器が存在しない場合、異常を投げる
        Device deviceDB = deviceRepository.findById(deviceId).orElseThrow(() -> new IllegalArgumentException("device:" + deviceId + "not exist."));

        deviceDTO.setUpdateTime(LocalDateTime.now()); // 更新日時の設定

//        System.out.println(deviceDTO);

        // 更新アクション
        deviceRepository.updateDevice(
                deviceId,
                deviceDTO.getDeviceModel(),
                deviceDTO.getComputerName(),
                deviceDTO.getLoginUsername(),
                deviceDTO.getProject(),
                deviceDTO.getDevRoom(),
                deviceDTO.getUserId(),
                deviceDTO.getRemark(),
                deviceDTO.getSelfConfirmId(),
                deviceDTO.getOsId(),
                deviceDTO.getMemoryId(),
                deviceDTO.getSsdId(),
                deviceDTO.getHddId(),
                deviceDTO.getUpdateTime(),
                deviceDTO.getUpdater()
        );

        /***
         * JPAのL 1キャッシュの問題で、ここで返されるデータはキャッシュから取得した上のdeviceDBのデータであり、
         * repositoryの@Modifying注記に@Modifying（clearAutomatically=true、flushAutomatically=true）を追加する必要があります。
         * キャッシュをクリアして、新しいデータを取得できます
         */
        // 更新されたデータを返す
        Device deviceReturn = deviceRepository.findById(deviceId).orElseThrow(() -> new IllegalStateException("device:" + deviceId + "not exist."));


        // monitor update
        List<Monitor> monitorReturns = new ArrayList<>();
        if (!CollectionUtils.isEmpty(deviceFullDTO.getMonitors())) {
            for (MonitorDTO monitorDTO : deviceFullDTO.getMonitors()) {

                monitorDTO.setDeviceId(deviceFullDTO.getDeviceId());
                monitorDTO.setCreater(deviceFullDTO.getCreater());
                monitorDTO.setUpdater(deviceFullDTO.getUpdater());

                // exist
                if(monitorRepository.existsByMonitorName(monitorDTO.getMonitorName())) {

                    Monitor monitorDB = monitorRepository.findByMonitorName(monitorDTO.getMonitorName());

                    // be used
                    if (!monitorDB.getDeviceId().equals(deviceId)) {
                        throw new IllegalStateException("Monitor " + monitorDTO.getMonitorName() + " is used by " + monitorDB.getDeviceId() + "!");
                    }

                    // モニタは変更されていません
                    monitorDB.setMonitorName(monitorDTO.getMonitorName());
                    monitorDB.setUpdater(monitorDTO.getUpdater());
                    monitorDB.setUpdateTime(LocalDateTime.now());

                    Monitor monitorReturn = monitorRepository.save(monitorDB);
                    monitorReturns.add(monitorReturn);

                } else { // not exist
                    Monitor monitor = convertMonitorToEntity(monitorDTO);

                    monitor.setCreateTime(LocalDateTime.now());
                    monitor.setUpdateTime(LocalDateTime.now());


                    Monitor monitorReturn = monitorRepository.save(monitor); // update
                    monitorReturns.add(monitorReturn);
                }
            }
        }


        // ip update
        List<DeviceIp> ipReturns = new ArrayList<>();
        if (!CollectionUtils.isEmpty(deviceFullDTO.getIpAddresses())) {
            for (DeviceIpDTO deviceIpDTO : deviceFullDTO.getIpAddresses()) {

                deviceIpDTO.setDeviceId(deviceFullDTO.getDeviceId());
                deviceIpDTO.setCreater(deviceFullDTO.getCreater());
                deviceIpDTO.setUpdater(deviceFullDTO.getUpdater());

                // ipチェック
                String ipAddress = deviceIpDTO.getIpAddress();
                ipAddress = ipAddress.trim();

                try {
                    InetAddress.getByName(ipAddress);
                } catch (Exception e) {
                    throw new IllegalArgumentException("IPアドレスの形式が無効です: " + ipAddress);
                }

                // update
                if (deviceIpRepository.existsByIpAddress(deviceIpDTO.getIpAddress())) {
                    DeviceIp deviceIpDB = deviceIpRepository.findByIpAddress(deviceIpDTO.getIpAddress());

                    // be used
                    if (!deviceIpDB.getDeviceId().equals(deviceId)) {
                        throw new IllegalStateException("IP " + deviceIpDTO.getIpAddress() + " is used by " + deviceIpDB.getDeviceId() + "!");
                    }

                    // ipは変更されていません
                    deviceIpDB.setIpAddress(deviceIpDTO.getIpAddress());
                    deviceIpDB.setUpdater(deviceIpDTO.getUpdater());
                    deviceIpDB.setUpdateTime(LocalDateTime.now());

                    DeviceIp deviceIpReturn = deviceIpRepository.save(deviceIpDB);
                    ipReturns.add(deviceIpReturn);

                } else { // not exist
                    DeviceIp deviceIp = convertDeviceIpToEntity(deviceIpDTO);
                    deviceIp.setCreateTime(LocalDateTime.now());
                    deviceIp.setUpdateTime(LocalDateTime.now());

                    DeviceIp deviceIpReturn = deviceIpRepository.save(deviceIp);
                    ipReturns.add(deviceIpReturn);
                }
            }
        }


        DeviceFullDTO deviceFullDtoReturn = DeviceFullDTO.builder()
                .deviceId(deviceReturn.getDeviceId()).deviceModel(deviceReturn.getDeviceModel())
                .computerName(deviceReturn.getComputerName()).loginUsername(deviceReturn.getLoginUsername())
                .project(deviceReturn.getProject()).devRoom(deviceReturn.getDevRoom()).userId(deviceReturn.getUserId())
                .remark(deviceReturn.getRemark()).selfConfirmId(deviceReturn.getSelfConfirmId()).osId(deviceReturn.getOsId())
                .memoryId(deviceReturn.getMemoryId()).ssdId(deviceReturn.getSsdId()).hddId(deviceReturn.getHddId())
                .creater(deviceReturn.getCreater()).updater(deviceReturn.getUpdater())
                .monitors(convertMonitorsToDTOList(monitorReturns))
                .ipAddresses(convertDeviceIpsToDTOList(ipReturns))
                .build();

        return deviceFullDtoReturn;
    }


    // MonitorList 回転 MonitorDTOList
    private List<MonitorDTO> convertMonitorsToDTOList(List<Monitor> monitors) {
        if (CollectionUtils.isEmpty(monitors)) {
            return new ArrayList<>();
        }

        return monitors.stream()
                .map(this::convertMonitorToDTO)
                .collect(Collectors.toList());
    }

    // MonitorDTO 回転 Monitor
    private Monitor convertMonitorToEntity(MonitorDTO dto) {
        Monitor monitor = new Monitor();

        monitor.setMonitorId(dto.getMonitorId());
        monitor.setMonitorName(dto.getMonitorName());
        monitor.setDeviceId(dto.getDeviceId());
        monitor.setCreateTime(dto.getCreateTime());
        monitor.setCreater(dto.getCreater());
        monitor.setUpdateTime(dto.getUpdateTime());
        monitor.setUpdater(dto.getUpdater());

        return monitor;
    }

    // Monitor 回転 MonitorDTO
    private MonitorDTO convertMonitorToDTO(Monitor monitor) {
        return MonitorDTO.builder()
                .monitorId(monitor.getMonitorId())
                .monitorName(monitor.getMonitorName())
                .deviceId(monitor.getDeviceId())
                .createTime(monitor.getCreateTime())
                .creater(monitor.getCreater())
                .updateTime(monitor.getUpdateTime())
                .updater(monitor.getUpdater())
                .build();
    }

    // DeviceIpList 回転 DeviceIpDTOList
    private List<DeviceIpDTO> convertDeviceIpsToDTOList(List<DeviceIp> deviceIps) {
        if (CollectionUtils.isEmpty(deviceIps)) {
            return new ArrayList<>();
        }

        return deviceIps.stream()
                .map(this::convertDeviceIpToDTO)
                .collect(Collectors.toList());
    }

    // DeviceIpDTO 回転 DeviceIp
    private DeviceIp convertDeviceIpToEntity(DeviceIpDTO dto) {
        DeviceIp deviceIp = new DeviceIp();

        deviceIp.setIpId(dto.getIpId());
        deviceIp.setIpAddress(dto.getIpAddress());
        deviceIp.setDeviceId(dto.getDeviceId());
        deviceIp.setCreateTime(dto.getCreateTime());
        deviceIp.setCreater(dto.getCreater());
        deviceIp.setUpdateTime(dto.getUpdateTime());
        deviceIp.setUpdater(dto.getUpdater());

        return deviceIp;
    }

    // DeviceIp 回転 DeviceIpDTO
    private DeviceIpDTO convertDeviceIpToDTO(DeviceIp deviceIp) {

        return DeviceIpDTO.builder()
                .ipId(deviceIp.getIpId())
                .ipAddress(deviceIp.getIpAddress())
                .deviceId(deviceIp.getDeviceId())
                .createTime(deviceIp.getCreateTime())
                .creater(deviceIp.getCreater())
                .updateTime(deviceIp.getUpdateTime())
                .updater(deviceIp.getUpdater())
                .build();
    }

    // DeviceDTO 回転 Device
    private Device convertDeviceToEntity(DeviceDTO dto) {
        Device device = new Device();

        device.setDeviceId(dto.getDeviceId());
        device.setDeviceModel(dto.getDeviceModel());
        device.setComputerName(dto.getComputerName());
        device.setLoginUsername(dto.getLoginUsername());
        device.setProject(dto.getProject());
        device.setDevRoom(dto.getDevRoom());
        device.setUserId(dto.getUserId());
        device.setRemark(dto.getRemark());
        device.setSelfConfirmId(dto.getSelfConfirmId());
        device.setOsId(dto.getOsId());
        device.setMemoryId(dto.getMemoryId());
        device.setSsdId(dto.getSsdId());
        device.setHddId(dto.getHddId());
        device.setCreateTime(dto.getCreateTime());
        device.setCreater(dto.getCreater());
        device.setUpdateTime(dto.getUpdateTime());
        device.setUpdater(dto.getUpdater());

        return device;
    }

    // Device 回転 DeviceDTO
    private DeviceDTO convertDeviceToDTO(Device device) {
        return DeviceDTO.builder()
                .deviceId(device.getDeviceId()).deviceModel(device.getDeviceModel())
                .computerName(device.getComputerName()).loginUsername(device.getLoginUsername())
                .project(device.getProject()).devRoom(device.getDevRoom()).userId(device.getUserId())
                .remark(device.getRemark()).selfConfirmId(device.getSelfConfirmId()).osId(device.getOsId())
                .memoryId(device.getMemoryId()).ssdId(device.getSsdId()).hddId(device.getHddId())
                .createTime(device.getCreateTime()).creater(device.getCreater()).updateTime(device.getUpdateTime())
                .updater(device.getUpdater())
                .build();
    }


    /**
     * コンストラクタ
     * @param deviceRepository デバイスリポジトリ
     */
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * デバイス削除
     * @param deviceId デバイスID
     */
    public void deleteDevice(String deviceId) {
        log.info("Delete device with id: {}", deviceId);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found, DeviceId: " + deviceId));

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
     * 最初のモニターIDを取得
     * @param device デバイスエンティティ
     * @return 最初のモニターID
     */
    private String getFirstMonitorId(Device device) {
        if (device.getMonitorInfos() != null && !device.getMonitorInfos().isEmpty()) {
            Monitor monitor = device.getMonitorInfos().get(0);
            return monitor.getMonitorId() != null ?
                    String.valueOf(monitor.getMonitorId()) : "";
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
     * 最初のIPアドレスを取得
     * @param device デバイスエンティティ
     * @return 最初のIPアドレス
     */
    private String getFirstIpAddress(Device device) {
        if (device.getDeviceIps() != null && !device.getDeviceIps().isEmpty()) {
            DeviceIp ip = device.getDeviceIps().get(0);
            return ip.getIpAddress() != null ? ip.getIpAddress() : "";
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


    @PersistenceContext
    private EntityManager entityManager;

        // 全ての重複しない開発室名を取得
    public List<String> getAllDevRooms() {
        String jpql = "SELECT DISTINCT d.devRoom FROM Device d WHERE d.devRoom IS NOT NULL AND TRIM(d.devRoom) != '' ORDER BY d.devRoom";

        try {
            List<String> devRooms = entityManager.createQuery(jpql, String.class)
                    .getResultList();

            // 空値をフィルタリングし、前後の空白を除去
            return devRooms.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取开发室列表时出错", e);
            return Collections.emptyList();
        }
    }

    // 全ての重複しないプロジェクト名を取得
    public List<String> getAllProjects() {
        String jpql = "SELECT DISTINCT d.project FROM Device d WHERE d.project IS NOT NULL AND TRIM(d.project) != '' ORDER BY d.project";

        try {
            List<String> projects = entityManager.createQuery(jpql, String.class)
                    .getResultList();

            // 空値をフィルタリングし、前後の空白を除去
            return projects.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取项目列表时出错", e);
            return Collections.emptyList();
        }
    }

    // デバイス一覧（ページングとフィルタリング）
    public Page<DeviceDTO> list(String deviceName, String userId, String userName, String project, String devRoom, int page, int size) {
        // ページ数の調整：ページ番号1から始まる
        page = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("deviceId").ascending());

        // デバイスを直接クエリ（ユーザーテーブルをLEFT JOIN）
        StringBuilder jpql = new StringBuilder(
                "SELECT d FROM Device d " +
                        "LEFT JOIN FETCH d.user u " +
                        "LEFT JOIN FETCH d.osDict " +
                        "LEFT JOIN FETCH d.memoryDict " +
                        "LEFT JOIN FETCH d.ssdDict " +
                        "LEFT JOIN FETCH d.hddDict " +
                        "LEFT JOIN FETCH d.selfConfirmDict " +
                        "WHERE 1=1 "
        );

        if (StringUtils.hasText(deviceName)) {
            jpql.append("AND d.computerName LIKE :deviceName ");
        }
        if (StringUtils.hasText(userId)) {
            jpql.append("AND d.userId = :userId ");
        }
        if (StringUtils.hasText(userName)) {
            jpql.append("AND u.userName LIKE :userName ");
        }
        if (StringUtils.hasText(project)) {
            jpql.append("AND d.project LIKE :project ");
        }
        if (StringUtils.hasText(devRoom)) {
            jpql.append("AND d.devRoom LIKE :devRoom ");
        }

        TypedQuery<Device> query = entityManager.createQuery(jpql.toString(), Device.class);

        if (StringUtils.hasText(deviceName)) {
            query.setParameter("deviceName", "%" + deviceName + "%");
        }
        if (StringUtils.hasText(userId)) {
            query.setParameter("userId", userId);
        }
        if (StringUtils.hasText(userName)) {
            query.setParameter("userName", "%" + userName + "%");
        }
        if (StringUtils.hasText(project)) {
            query.setParameter("project", "%" + project + "%");
        }
        if (StringUtils.hasText(devRoom)) {
            query.setParameter("devRoom", "%" + devRoom + "%");
        }

        // ページング処理
        int totalRows = getTotalCount(deviceName, userId, userName, project, devRoom);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Device> devices = query.getResultList();
        if (devices.isEmpty()) {
            return Page.empty(pageable);
        }

        // 関連データをバッチでロード
        List<String> deviceIds = devices.stream()
                .map(d -> d.getDeviceId().trim())
                .collect(Collectors.toList());

        Map<String, List<DeviceIp>> ipMap = getDeviceIpMap(deviceIds);
        Map<String, List<Monitor>> monitorMap = getDeviceMonitorMap(deviceIds);

        // DTOに変換
        List<DeviceDTO> dtoList = devices.stream()
                .map(device -> toDTOWithRelations(device, ipMap, monitorMap))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, totalRows);
    }

    // 総レコード数を取得
    private int getTotalCount(String deviceName, String userId, String userName, String project, String devRoom) {
        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(d) FROM Device d " +
                        "LEFT JOIN d.user u " +
                        "WHERE 1=1 "
        );

        if (StringUtils.hasText(deviceName)) {
            jpql.append("AND d.computerName LIKE :deviceName ");
        }
        if (StringUtils.hasText(userId)) {
            jpql.append("AND d.userId = :userId ");
        }
        if (StringUtils.hasText(userName)) {
            jpql.append("AND u.userName LIKE :userName ");
        }
        if (StringUtils.hasText(project)) {
            jpql.append("AND d.project LIKE :project ");
        }
        if (StringUtils.hasText(devRoom)) {
            jpql.append("AND d.devRoom LIKE :devRoom ");
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

        if (StringUtils.hasText(deviceName)) {
            query.setParameter("deviceName", "%" + deviceName + "%");
        }
        if (StringUtils.hasText(userId)) {
            query.setParameter("userId", userId);
        }
        if (StringUtils.hasText(userName)) {
            query.setParameter("userName", "%" + userName + "%");
        }
        if (StringUtils.hasText(project)) {
            query.setParameter("project", "%" + project + "%");
        }
        if (StringUtils.hasText(devRoom)) {
            query.setParameter("devRoom", "%" + devRoom + "%");
        }

        return query.getSingleResult().intValue();
    }

    // UserServiceで呼び出し用：デバイス名またはユーザーIDでフィルタリングしてユニークなユーザーIDリストを取得
    public List<String> findUserIdsByCondition(String deviceName, String userId) {
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT d.userId FROM Device d WHERE d.userId IS NOT NULL ");
        if (StringUtils.hasText(deviceName)) {
            jpql.append("AND d.computerName LIKE :deviceName ");
        }
        if (StringUtils.hasText(userId)) {
            jpql.append("AND d.userId = :userId ");
        }

        TypedQuery<String> query = entityManager.createQuery(jpql.toString(), String.class);
        if (StringUtils.hasText(deviceName)) {
            query.setParameter("deviceName", "%" + deviceName + "%");
        }
        if (StringUtils.hasText(userId)) {
            query.setParameter("userId", userId);
        }
        return query.getResultList();
    }

    // デバイス詳細クエリ、CHAR型のTRIM処理
    public DeviceDTO detail(String deviceId) {
        if (!StringUtils.hasText(deviceId)) return null;

        String jpql = "SELECT d FROM Device d " +
                "LEFT JOIN FETCH d.user u " +
                "LEFT JOIN FETCH d.osDict " +
                "LEFT JOIN FETCH d.memoryDict " +
                "LEFT JOIN FETCH d.ssdDict " +
                "LEFT JOIN FETCH d.hddDict " +
                "LEFT JOIN FETCH d.selfConfirmDict " +
                "LEFT JOIN FETCH u.userTypeDict " +
                "WHERE TRIM(d.deviceId) = :deviceId";

        try {
            Device device = entityManager.createQuery(jpql, Device.class)
                    .setParameter("deviceId", deviceId.trim())
                    .getSingleResult();

            List<String> ids = Collections.singletonList(device.getDeviceId().trim());
            return toDTOWithRelations(device, getDeviceIpMap(ids), getDeviceMonitorMap(ids));
        } catch (Exception e) {
            log.error("デバイス詳細が見つかりません: {}", deviceId);
            return null;
        }
    }

    // 単一ユーザーの全デバイスを取得
    public List<DeviceDTO> getDevicesByUserId(String userId) {
        if (!StringUtils.hasText(userId)) return new ArrayList<>();
        Map<String, List<DeviceDTO>> resultMap = getDeviceMapByUserIds(Collections.singletonList(userId.trim()));
        return resultMap.getOrDefault(userId.trim(), new ArrayList<>());
    }

    // バッチでユーザーデバイスマッピングを取得
    public Map<String, List<DeviceDTO>> getDeviceMapByUserIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();

        String jpql = "SELECT d FROM Device d " +
                "LEFT JOIN FETCH d.user u " +
                "LEFT JOIN FETCH d.osDict " +
                "LEFT JOIN FETCH d.memoryDict " +
                "LEFT JOIN FETCH d.ssdDict " +
                "LEFT JOIN FETCH d.hddDict " +
                "LEFT JOIN FETCH d.selfConfirmDict " +
                "WHERE d.userId IN :userIds";

        List<Device> allDevices = entityManager.createQuery(jpql, Device.class)
                .setParameter("userIds", userIds)
                .getResultList();

        if (allDevices.isEmpty()) return Collections.emptyMap();

        List<String> deviceIds = allDevices.stream()
                .map(d -> d.getDeviceId().trim())
                .collect(Collectors.toList());

        Map<String, List<DeviceIp>> ipMap = getDeviceIpMap(deviceIds);
        Map<String, List<Monitor>> monitorMap = getDeviceMonitorMap(deviceIds);

        return allDevices.stream()
                .map(dev -> toDTOWithRelations(dev, ipMap, monitorMap))
                .collect(Collectors.groupingBy(DeviceDTO::getUserId));
    }

    // フィールド欠落防止
    private DeviceDTO toDTOWithRelations(Device device, Map<String, List<DeviceIp>> ipMap, Map<String, List<Monitor>> monitorMap) {
        DeviceDTO dto = toBasicDTO(device);
        String key = device.getDeviceId().trim();

        // IPリストを設定
        List<DeviceIp> ips = ipMap.getOrDefault(key, new ArrayList<>());
        dto.setDeviceIps(ips.stream().map(ip -> {
            DeviceIpDTO ipDto = new DeviceIpDTO();
            ipDto.setIpId(ip.getIpId());
            ipDto.setIpAddress(ip.getIpAddress());
            ipDto.setDeviceId(key); // 関連ID
            ipDto.setCreateTime(ip.getCreateTime());
            ipDto.setCreater(ip.getCreater());
            ipDto.setUpdateTime(ip.getUpdateTime());
            ipDto.setUpdater(ip.getUpdater());
            return ipDto;
        }).collect(Collectors.toList()));

        // 集約IP文字列を設定
        dto.setIpAddresses(ips.stream().map(DeviceIp::getIpAddress).collect(Collectors.joining(", ")));

        // モニターリストを設定
        List<Monitor> monitors = monitorMap.getOrDefault(key, new ArrayList<>());
        dto.setMonitors(monitors.stream().map(m -> {
            MonitorDTO mDto = new MonitorDTO();
            mDto.setMonitorId(m.getMonitorId());
            mDto.setMonitorName(m.getMonitorName());
            mDto.setDeviceId(key); // 関連ID
            mDto.setCreateTime(m.getCreateTime());
            mDto.setCreater(m.getCreater());
            mDto.setUpdateTime(m.getUpdateTime());
            mDto.setUpdater(m.getUpdater());
            return mDto;
        }).collect(Collectors.toList()));

        // 集計情報を設定
        dto.setIpCount(ips.size());
        dto.setMonitorCount(monitors.size());

        return dto;
    }

    private DeviceDTO toBasicDTO(Device device) {
        DeviceDTO dto = DeviceDTO.builder()
                .deviceId(device.getDeviceId().trim())
                .userId(device.getUserId())
                .userInfo(device.getUser() != null ? UserDto.builder()
                        .userId(device.getUser().getUserId())
                        .deptId(device.getUser().getDeptId())
                        .name(device.getUser().getName())
                        .build() : null)
                .deviceModel(device.getDeviceModel())
                .computerName(device.getComputerName())
                .loginUsername(device.getLoginUsername())
                .project(device.getProject())
                .devRoom(device.getDevRoom())
                .remark(device.getRemark())
                .osDict(DictMapper.toDTO(device.getOsDict()))
                .osId(device.getOsDict() != null ? device.getOsDict().getDictId() : null)
                .memoryDict(DictMapper.toDTO(device.getMemoryDict()))
                .memoryId(device.getMemoryDict() != null ? device.getMemoryDict().getDictId() : null)
                .ssdDict(DictMapper.toDTO(device.getSsdDict()))
                .ssdId(device.getSsdDict() != null ? device.getSsdDict().getDictId() : null)
                .hddDict(DictMapper.toDTO(device.getHddDict()))
                .hddId(device.getHddDict() != null ? device.getHddDict().getDictId() : null)
                .selfConfirmDict(DictMapper.toDTO(device.getSelfConfirmDict()))
                .createTime(device.getCreateTime())
                .creater(device.getCreater())
                .updateTime(device.getUpdateTime())
                .updater(device.getUpdater())
                .build();

        // ハードウェア構成サマリーを生成
        StringBuilder summary = new StringBuilder();
        if (dto.getOsDict() != null && dto.getOsDict().getDictItemName() != null) {
            summary.append("操作系统: ").append(dto.getOsDict().getDictItemName()).append(" | ");
        }
        if (dto.getMemoryDict() != null && dto.getMemoryDict().getDictItemName() != null) {
            summary.append("内存: ").append(dto.getMemoryDict().getDictItemName()).append(" | ");
        }
        if (dto.getSsdDict() != null && dto.getSsdDict().getDictItemName() != null) {
            summary.append("固态硬盘: ").append(dto.getSsdDict().getDictItemName()).append(" | ");
        }
        if (dto.getHddDict() != null && dto.getHddDict().getDictItemName() != null) {
            summary.append("机械硬盘: ").append(dto.getHddDict().getDictItemName());
        }

        // 末尾の" | "を削除
        if (summary.length() > 0 && summary.toString().endsWith(" | ")) {
            summary.setLength(summary.length() - 3);
        }
        dto.setHardwareSummary(summary.toString());

        return dto;
    }

    // プライベートヘルパークエリ、関連データを処理
    private Map<String, List<DeviceIp>> getDeviceIpMap(List<String> deviceIds) {
        if (deviceIds.isEmpty()) return Collections.emptyMap();
        // SQLレイヤーでTRIM処理
        String jpql = "SELECT TRIM(ip.device.deviceId), ip FROM DeviceIp ip WHERE ip.device.deviceId IN :deviceIds";
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .setParameter("deviceIds", deviceIds)
                .getResultList();

        Map<String, List<DeviceIp>> map = new HashMap<>();
        for (Object[] row : results) {
            String id = ((String) row[0]).trim();
            map.computeIfAbsent(id, k -> new ArrayList<>()).add((DeviceIp) row[1]);
        }
        return map;
    }

    private Map<String, List<Monitor>> getDeviceMonitorMap(List<String> deviceIds) {
        if (deviceIds.isEmpty()) return Collections.emptyMap();
        String jpql = "SELECT TRIM(m.device.deviceId), m FROM Monitor m WHERE m.device.deviceId IN :deviceIds";
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .setParameter("deviceIds", deviceIds)
                .getResultList();

        Map<String, List<Monitor>> map = new HashMap<>();
        for (Object[] row : results) {
            String id = ((String) row[0]).trim();
            map.computeIfAbsent(id, k -> new ArrayList<>()).add((Monitor) row[1]);
        }
        return map;
    }
}