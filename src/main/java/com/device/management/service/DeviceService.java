package com.device.management.service;

import com.device.management.entity.Device;
import com.device.management.entity.DeviceIp;
import com.device.management.entity.Monitor;
import com.device.management.entity.User;
import com.device.management.repository.DeviceRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * デバイスサービスクラス
 * デバイス関連のビジネスロジックを処理
 */
@Slf4j
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

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
}