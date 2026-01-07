package com.device.management.service;

import com.device.management.entity.*;
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

@Slf4j
@Service
public class DemoService {

    private final DeviceRepository deviceRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public DemoService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public void deleteDevice(String deviceId) {
        log.info("Delete device {}", deviceId);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("device not found,DeviceId:" + deviceId));

        deviceRepository.deleteById(deviceId);

        log.info("Delete device success {}", deviceId);
    }

    /**
     * excel 生成
     *
     */
    public void exportDevicesToExcel(HttpServletResponse response) {
        List<Device> devices = deviceRepository.findAll();
        log.info("Exporting {] devices to Excel", devices.size());

        String timeStamp = LocalDateTime.now().format(formatter);
        String fileName = timeStamp + ".xlsx";

        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.info("文件名编码错误，使用原始文件名", e);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=UTF-8" + fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Devices");

            createExcelHeader(sheet);

            if (!devices.isEmpty()) {
                fillExclData(sheet, devices);
            } else {
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("Device");
            }

            autoSizeColumns(sheet);

        } catch (IOException e) {

        }

    }

    private void autoSizeColumns(Sheet sheet) {
        if (sheet.getRow(0)!=null){
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

    private void createExcelHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "工号", "姓名", "部门",
                "主机设备编号", "显示器设备编号", "显示器设备名", "主机型号", "电脑名",
                "IP　地址", "操作系统", "内存单位", "固态硬盘",
                "机械硬盘", "登录用户名", "所在项目", "所在开发室", "备注", "本人确认"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private void fillExclData(Sheet sheet, List<Device> devices) {

        int rowNum = 1;
        for (Device device : devices) {
            Row row = sheet.createRow(rowNum++);
            User user = device.getUser();
            safeSetCellValue(row, 0, user != null ? user.getUserId() : "");
            safeSetCellValue(row, 1, user != null ? user.getName() : "");
            safeSetCellValue(row, 2, user != null ? user.getDeptId() : "");
            safeSetCellValue(row, 3, device.getDeviceId());
            safeSetCellValue(row, 4, getALlMonitorIds(device));
            safeSetCellValue(row, 5, getALlMonitorNames(device));
            safeSetCellValue(row, 6, device.getDeviceModel());
            safeSetCellValue(row, 7, device.getComputerName());
            safeSetCellValue(row, 8, getAlladdresses(device));
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

    private String getDictItemName(Dict dict) {
        return dict != null && dict.getDictItemName() != null ? dict.getDictItemName() : "";
    }

    private String getAlladdresses(Device device) {
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


    private void safeSetCellValue(Row row, int cellNum, String value) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value);
    }

    private String getALlMonitorIds(Device device) {
        if (device.getMonitorInfos() == null && !device.getMonitorInfos().isEmpty()) {
            //判断是否为空
            StringBuilder monitorIds = new StringBuilder();
            //空容器
            for (Monitor monitor : device.getMonitorInfos()) {
                //循环
                if (monitor.getMonitorId() != null) {
                    //判空
                    if (monitorIds.length() > 0) {
                        monitorIds.append(", ");
                        //分割
                    }
                    monitorIds.append(monitor.getMonitorId());
                }
            }
            return monitorIds.toString();
        }
        return "";
    }

    private String getALlMonitorNames(Device device) {
        if (device.getMonitorInfos() == null && !device.getMonitorInfos().isEmpty()) {
            //判断是否为空
            StringBuilder monitorNames = new StringBuilder();
            //空容器
            for (Monitor monitor : device.getMonitorInfos()) {
                //循环
                if (monitor.getMonitorName() != null && !monitor.getMonitorName().isEmpty()) {
                    //判空
                    if (monitorNames.length() > 0) {
                        monitorNames.append(", ");
                        //分割
                    }
                    monitorNames.append(monitor.getMonitorName());
                }
            }
            return monitorNames.toString();
        }
        return "";
    }



}
