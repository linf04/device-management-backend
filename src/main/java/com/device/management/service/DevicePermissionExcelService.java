package com.device.management.service;

import com.device.management.dto.DevicePermissionExcelVo;
import com.device.management.entity.Dict;
import com.device.management.repository.DevicePermissionRepository;
import com.device.management.repository.DictRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DevicePermissionExcelService {
    // 列幅(文字数)を定義します
    private static final int[] COLUMN_WIDTHS = {8,   //编号
            30,  //機器番号
            20,  //コンピュータ名
            20,  //機器番号
            25,  //IPアドレス
            10,  //社員番号
            10,  //名前
            10,  //役職
            15,  //ログインユーザー名
            10,  //ドメイン名
            15,  //ドメイングループ名
            15,  //不加ドメイン理由
            15,  //SmartIT状態
            20,  //SmartITインストール理由
            12,  //USB状態
            20,  //USB開通理由
            15,  //使用期限
            12,  //接続状態
            20,  //Symantecなし理由
            30   //備考
    };
    @Resource
    DevicePermissionRepository devicePermissionRepository;
    private Map<Long, String> dictCache;
    @Resource
    private DictRepository dictRepository;

    @PostConstruct
    public void initDictCache() {
        List<Dict> dictList = dictRepository.findAll();
        dictCache = new HashMap<>();
        for (Dict dict : dictList) {
            Long key = dict.getDictId();
            dictCache.put(key, dict.getDictItemName());
        }
    }

    // 文字列をIDから名称に変換するユーティリティメソッド
    private String getDictName(Long key) {
        return dictCache.get(key);
    }

    /**
     * 設備使用許可リストをエクスポートする
     */
    public void exportDevicePermissionList(List<DevicePermissionExcelVo> dataList, HttpServletResponse response) throws IOException {
        // 作成するワークブック
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("设备使用权限清单");

        // スタイルを作成する
        Map<String, CellStyle> styles = createStyles(workbook);

        // 列幅を設定する
        setColumnWidths(sheet);

        // 表を作成する
        int currentRow = buildExcelTemplate(sheet, styles, dataList);

        // 応答ヘッダを設定する
        String fileName = "设备使用权限清单_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"");

        // 出力ストリームに書き込む
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 各種スタイルを作成する
     */
    private Map<String, CellStyle> createStyles(Workbook workbook) {
        Map<String, CellStyle> styles = new java.util.HashMap<>();

        // 2.表題のスタイル（背景は灰色、太の字）
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setWrapText(true); // 自动换行 | 自動改行
        styles.put("header", headerStyle);

        // 3. データのスタイル（デフォルト）
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setWrapText(true);
        styles.put("data", dataStyle);

        // 4. 中央寄せデータのスタイル
        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.cloneStyleFrom(dataStyle);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        styles.put("center", centerStyle);

        // 5. 備考のスタイル（斜体）
        CellStyle remarkStyle = workbook.createCellStyle();
        Font remarkFont = workbook.createFont();
        remarkFont.setItalic(true);
        remarkStyle.setFont(remarkFont);
        remarkStyle.cloneStyleFrom(dataStyle);
        styles.put("remark", remarkStyle);

        // 6. 日付のスタイル
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.cloneStyleFrom(dataStyle);
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        styles.put("date", dateStyle);

        return styles;
    }

    /**
     * 列の幅を設定する
     */
    private void setColumnWidths(Sheet sheet) {
        for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
            sheet.setColumnWidth(i, COLUMN_WIDTHS[i] * 256);
        }
    }

    /**
     * Excelテンプレートを構築する
     */
    private int buildExcelTemplate(Sheet sheet, Map<String, CellStyle> styles, List<DevicePermissionExcelVo> dataList) {
        int rowNum = 0;

        //  6行目：大見出し（結合セル）
        Row row5 = sheet.createRow(rowNum++);
        createCell(row5, 0, "编号", styles.get("header"));
        createMergedCell(sheet, row5, 1, 4, "设备", styles.get("header"));
        createMergedCell(sheet, row5, 5, 8, "使用者/责任人", styles.get("header"));
        createMergedCell(sheet, row5, 9, 11, "域", styles.get("header"));
        createMergedCell(sheet, row5, 12, 13, "SmartIT安装", styles.get("header"));
        createMergedCell(sheet, row5, 14, 16, "USB设备", styles.get("header"));
        createMergedCell(sheet, row5, 17, 18, "防病毒", styles.get("header"));
        createCell(row5, 19, "备注", styles.get("header"));

        // 詳細見出し
        Row row6 = sheet.createRow(rowNum++);
        String[] headers = {"", "设备编号", "电脑名", "显示器", "IP地址", "工号", "姓名", "级别", "登录用户名", "域名", "域内组名", "不加域理由", "SmartIT状态", "不安装SmartIT理由", "USB状态", "USB开通理由", "使用截止日期", "连接状态", "无Symantec理由", ""};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = row6.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.get("header"));
        }

        // データ行を埋める
        rowNum = fillDataRows(sheet, styles, dataList, rowNum);

        return rowNum;
    }

    /**
     * データ行を埋める
     */
    private int fillDataRows(Sheet sheet, Map<String, CellStyle> styles, List<DevicePermissionExcelVo> dataList, int startRow) {
        int rowNum = startRow;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < dataList.size(); i++) {
            DevicePermissionExcelVo item = dataList.get(i);
            Row row = sheet.createRow(rowNum++);

            //番号
            createCell(row, 0, String.valueOf(i + 1), styles.get("center"));

            //設備情報
            createCell(row, 1, item.getDeviceId(), styles.get("data"));
            createCell(row, 2, item.getComputerName(), styles.get("data"));
            createCell(row, 3, item.getMonitorName() != null ? item.getMonitorName().toString() : "", styles.get("data"));
            createCell(row, 4, item.getIpAddress() != null ? item.getIpAddress().toString() : "", styles.get("data"));

            //使用者情報
            createCell(row, 5, item.getUserId(), styles.get("center"));
            createCell(row, 6, item.getName(), styles.get("center"));
            createCell(row, 7, item.getDeptId(), styles.get("center"));
            createCell(row, 8, item.getLoginUsername(), styles.get("data"));

            //域情報
            createCell(row, 9, getDictName(item.getDomainStatusId()), styles.get("center"));
            createCell(row, 10, item.getDomainGroup(), styles.get("data"));
            createCell(row, 11, item.getNoDomainReason(), styles.get("data"));

            //SmartIT情報
            createCell(row, 12, getDictName(item.getSmartitStatusId()), styles.get("center"));
            createCell(row, 13, item.getNoSmartitReason(), styles.get("data"));

            //USB情報
            createCell(row, 14, getDictName(item.getUsbStatusId()), styles.get("center"));
            createCell(row, 15, item.getUsbReason(), styles.get("data"));
            createCell(row, 16, item.getUseExpireDate() != null ? item.getUseExpireDate().format(dateFormatter)  // LocalDate 直接调用 format 方法
                    : "", styles.get("date"));

            //その他情報
            createCell(row, 17, getDictName(item.getAntivirusStatusId()), styles.get("center"));
            createCell(row, 18, item.getNoSymantecReason(), styles.get("data"));
            createCell(row, 19, item.getRemark(), styles.get("remark"));

        }

        return rowNum;
    }

    /**
     * 結合セルを作成する
     */
    private void createMergedCell(Sheet sheet, Row row, int firstCol, int lastCol, String value, CellStyle style) {
        if (firstCol < lastCol) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol, lastCol));
        }
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    /**
     * 普通のセルを作成する
     */
    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    public List<DevicePermissionExcelVo> getDataFromDatabase() {
        return devicePermissionRepository.findAllDevicePermissionExcel();

    }


}