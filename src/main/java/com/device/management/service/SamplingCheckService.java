package com.device.management.service;

import com.device.management.dto.SamplingCheckDTO;
import com.device.management.entity.MonitorInfo;
import com.device.management.entity.SamplingCheck;
import com.device.management.mapper.SamplingCheckMapper;
import com.device.management.repository.MonitorInfoRepository;
import com.device.management.repository.SamplingCheckRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SamplingCheckService {
    @Autowired
    private SamplingCheckRepository samplingCheckRepository;
    @Autowired
    private SamplingCheckMapper samplingCheckMapper;
    @Autowired
    private MonitorInfoRepository monitorInfoRepository;

    //==================== アップデート機能関連 ========================================
    //更新方法
    public SamplingCheckDTO update(String samplingId, SamplingCheckDTO dto) {
        if(!samplingCheckRepository.existsById(samplingId))  throw new RuntimeException("記録が存在しないため、更新できません");
        SamplingCheck entity = samplingCheckRepository.findById(samplingId).get();
        samplingCheckMapper.updateEntityFromDto(dto,entity);
        entity.setUpdateTime(LocalDateTime.now());
        SamplingCheck saved = samplingCheckRepository.save(entity);

        return samplingCheckMapper.convertToDto(saved);
    }

    //==================== 新機能関連 ========================================
    //追加方法
    public SamplingCheckDTO create(SamplingCheckDTO dto) {
        SamplingCheck entity = samplingCheckMapper.convertToEntity(dto);
        entity.setSamplingId(UUID.randomUUID().toString().replace("-",""));
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        SamplingCheck saved = samplingCheckRepository.save(entity);

        return samplingCheckMapper.convertToDto(saved);
    }


    //==================== 機能関連の削除 ========================================
    //記録を削除する（記録が存在しない場合はエラー）
    public void delete(String samplingId) {
        log.info("delete sampling check {}", samplingId);
        if (!samplingCheckRepository.existsById(samplingId)) {
            throw new RuntimeException("Not found");
        }
        samplingCheckRepository.deleteById(samplingId);
    }

    public void deleteByDeviceId(String deviceId) {
        log.info("delete by device id {}", deviceId);
        samplingCheckRepository.deleteByDeviceId(deviceId);
    }

    //==================== 機能関連の検索 ========================================
    //IDで検索
    public SamplingCheckDTO findById(String id) {
        log.info("find sampling check by id {}", id);
        SamplingCheck samplingCheck = samplingCheckRepository.findById(id).orElse(null);
        return samplingCheckMapper.convertToDto(samplingCheck);
    }

    //報告番号で検索
    public List<SamplingCheckDTO> findByReportId(String reportId) {
        log.info("find sampling check by reportId {}", reportId);
        List<SamplingCheck> samplingChecks = samplingCheckRepository.findByReportId(reportId);
        //DTOに変換
        List<SamplingCheckDTO> dtoPage = samplingCheckMapper.convertToDto(samplingChecks);

        return dtoPage.stream()
                .peek(this::addMonitorInfo)
                .toList();
    }

    public List<SamplingCheckDTO> findAll() {
        Sort sort = Sort.by( Sort.Order.asc("name"),  Sort.Order.desc("updateTime") );
        List<SamplingCheck> samplingChecks;
        samplingChecks = samplingCheckRepository.findAll(sort);                                  //制約なし
        //DTOに変換
        List<SamplingCheckDTO> dtoPage = samplingCheckMapper.convertToDto(samplingChecks);

        return dtoPage.stream()
                .peek(this::addMonitorInfo)
                .toList();
    }

    //オプション条件によるレコードのフィルタ
    public Page<SamplingCheckDTO> findAllWithPageAndCondition(int page, int size, String deviceId, String userId) {
        log.info("get sampling checks {} {} {} {}", page, size, deviceId, userId);   //ログを記録する
        Sort sort = Sort.by( Sort.Order.asc("name"),  Sort.Order.desc("updateTime") );
        Pageable pageable = PageRequest.of(page - 1, size, sort);                //ページング要件の準備
        Page<SamplingCheck> samplingChecks;
        
        if (deviceId != null && !deviceId.isEmpty() && userId != null && !userId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByDeviceIdAndUserId(deviceId, userId, pageable);  //デバイスIDとユーザIDで調べる
        } else if (deviceId != null && !deviceId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByDeviceId(deviceId, pageable);                  //デバイスID別に調べる
        } else if (userId != null && !userId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByUserId(userId, pageable);                      //ユーザーID別に調べる
        } else {
            samplingChecks = samplingCheckRepository.findAll(pageable);                                  //制約なし
        }

        //DTOに変換してディスプレイ名を塗りつぶす
        Page<SamplingCheckDTO> dtoPage = samplingCheckMapper.convertToDto(samplingChecks);
        List<SamplingCheckDTO> contentWithMonitor = dtoPage.getContent().stream()
                .peek(this::addMonitorInfo)
                .collect(Collectors.toList());
        
        return new PageImpl<>(contentWithMonitor, pageable, dtoPage.getTotalElements());
    }

    private void addMonitorInfo(SamplingCheckDTO dto) {
        // デバイスのすべてのディスプレイを問い合わせる
        List<MonitorInfo> monitors = monitorInfoRepository.findByDeviceId(dto.getDeviceId());
        // すべてのモニタ名をセミコロンで接続
        if (!monitors.isEmpty()) {
            String monitorNames = monitors.stream()
                    .map(MonitorInfo::getMonitorName)
                    .filter(name -> name != null && !name.isEmpty())  // 过滤空值
                    .collect(Collectors.joining(";"));
            dto.setMonitorName(monitorNames);
        }
    }

    //==================== エクスポート機能関連 ========================================
    public void exportAll(String reportCode, HttpServletResponse response) throws IOException {
        //ブックの作成
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("工作现场检查");

        //ヘッダー、テーブル内容の書き込み
        setTableHead(sheet);
        List<SamplingCheckDTO> samplingCheckDTOS;
        if (reportCode != null && !reportCode.isEmpty()) samplingCheckDTOS = findByReportId(reportCode);
        else samplingCheckDTOS = findAll();
        setTableContent(sheet,1,samplingCheckDTOS);
        setTableStyle(workbook, sheet);

        // レスポンスヘッダの設定
        String encodedFileName = URLEncoder.encode( "月度检查表.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename*=UTF-8''" + encodedFileName );

        // ライト出力ストリーム
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    //ヘッダーの作成
    private void setTableHead(Sheet sheet) {
        Row row0 = sheet.createRow(0);
        Row row1 = sheet.createRow(1);

        // 最初の行ヘッダ
        row0.createCell(0).setCellValue("编号");
        row0.createCell(1).setCellValue("工号");
        row0.createCell(2).setCellValue("姓名");
        row0.createCell(3).setCellValue("设备编号");
        row0.createCell(4).setCellValue("检查项目");
        row0.createCell(10).setCellValue("处置措施");
        // 第2行ヘッダ
        row1.createCell(1).setCellValue("工号");
        row1.createCell(2).setCellValue("姓名");
        row1.createCell(4).setCellValue("开机认证");
        row1.createCell(5).setCellValue("密码屏保");
        row1.createCell(6).setCellValue("安装软件");
        row1.createCell(7).setCellValue("安全补丁");
        row1.createCell(8).setCellValue("病毒防护");
        row1.createCell(9).setCellValue("USB接口(封条)");

        //ヘッダーのマージ
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 9));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 10, 10));
    }

    //データベースレコードをテーブルに書き込む
    private void setTableContent(Sheet sheet,int endRow, List<SamplingCheckDTO> samplingCheckDTOS) {
        int rowNum = 0;
        for(SamplingCheckDTO samplingCheckDTO : samplingCheckDTOS) {
            Row row = sheet.createRow( ++endRow );
            row.createCell(0).setCellValue( ++rowNum );
            row.createCell(1).setCellValue( samplingCheckDTO.getUserId());
            row.createCell(2).setCellValue( samplingCheckDTO.getName());
            String monitorName = Arrays.stream(samplingCheckDTO.getMonitorName().split(";"))
                    .map(name-> name + "（显示器）")
                    .collect(Collectors.joining("\n"));
            String finalDeviceId = samplingCheckDTO.getDeviceId() + "\n" + monitorName;
            row.createCell(3).setCellValue( finalDeviceId );
            row.createCell(10).setCellValue( samplingCheckDTO.getDisposalMeasures());
            //OptionクラスでNULL値の可能性のあるフィールドを処理する
            Optional<Boolean> op1 = Optional.ofNullable(samplingCheckDTO.getBootAuthentication());
            if(op1.orElse(false)) row.createCell(4).setCellValue("○");

            Optional<Boolean> op2 = Optional.ofNullable(samplingCheckDTO.getScreenSaverPwd());
            if(op2.orElse(false)) row.createCell(5).setCellValue("○");

            Optional<Boolean> op3 = Optional.ofNullable(samplingCheckDTO.getInstalledSoftware());
            if(op3.orElse(false)) row.createCell(6).setCellValue("○");

            Optional<Boolean> op4 = Optional.ofNullable(samplingCheckDTO.getSecurityPatch());
            if(op4.orElse(false)) row.createCell(7).setCellValue("○");

            Optional<Boolean> op5 = Optional.ofNullable(samplingCheckDTO.getAntivirusProtection());
            if(op5.orElse(false)) row.createCell(8).setCellValue("○");

            Optional<Boolean> op6 = Optional.ofNullable(samplingCheckDTO.getUsbInterface());
            if(op6.orElse(false)) row.createCell(9).setCellValue("○");
        }
    }

    private void setTableStyle(Workbook book, Sheet sheet) {
        Font font = book.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        font.setBold(false);

        CellStyle style = book.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFont(font);
        for (Row row : sheet) {
            for (int i = 0; i <= 10; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) cell = row.createCell(i);
                cell.setCellStyle(style);
            }
        }
        sheet.setColumnWidth(0,3000);
        sheet.setColumnWidth(1,5000);
        sheet.setColumnWidth(2,5000);
        sheet.setColumnWidth(3,10000);
        for(int i=4;i<=9;i++){
            sheet.setColumnWidth(i, 3000);
        }
        sheet.setColumnWidth(10, 10000);
    }

    public void deleteByDeviceId(String deviceId) {
        log.info("delete by device id {}", deviceId);
        samplingCheckRepository.deleteByDeviceId(deviceId);
    }

}
