package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.service.SamplingCheckService;
import com.device.management.dto.SamplingCheckDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/security-checks")
public class SamplingCheckController {
    @Autowired
    private SamplingCheckService samplingCheckService;

    //xiaoshuang
    //レポートのエクスポート
    @GetMapping("/export")
    public void exportSamplingChecks(@RequestParam String reportCode,
                                     HttpServletResponse response) throws IOException {
        //ブックの作成
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("工作现场检查");

        //ヘッダー、テーブル内容の書き込み
        setTableHead(sheet);
        List<SamplingCheckDTO> samplingCheckDTOS = samplingCheckService.findByReportId(reportCode);
        setTableContent(sheet,1,samplingCheckDTOS);

        // レスポンスヘッダの設定
        String fileName = reportCode + "月度检查表.xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
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
    private void setTableContent(Sheet sheet, int endRow, List<SamplingCheckDTO> samplingCheckDTOS) {
        int rowNum = 0;
        for(SamplingCheckDTO samplingCheckDTO : samplingCheckDTOS) {
            Row row = sheet.createRow( ++endRow );
            row.createCell(0).setCellValue( ++rowNum );
            row.createCell(1).setCellValue( samplingCheckDTO.getUserId());
            row.createCell(2).setCellValue( samplingCheckDTO.getName());
            row.createCell(3).setCellValue( samplingCheckDTO.getDeviceId());
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



    //wangjunxi
    //詳細情報の取得
    @GetMapping("/{sampling_id}")
    public ApiResponse<SamplingCheckDTO> securityCheckQueryById(@PathVariable String sampling_id){
        SamplingCheckDTO samplingCheckDTO = samplingCheckService.findById(sampling_id);                  //Service層のクエリメソッドを呼び出す
        if(samplingCheckDTO==null){                                                                    //返り値に基づいて判断する
            return ApiResponse.error(404,"目標が存在しない");
        }
        else {
            return ApiResponse.success("検索成功",samplingCheckDTO);
        }
    }


    //liujiale
    //安全点検リストを取得
    @GetMapping
    public ApiResponse<List<SamplingCheckDTO>> getSamplingChecks(
            @RequestParam int page,                               //ページ番号を取得
            @RequestParam(defaultValue = "10") int size,         //1ページあたりに表示する情報件数（デフォルトは10件）
            @RequestParam(required = false) String deviceId,     //デバイスIDでフィルタリング可能
            @RequestParam(required = false) String userId) {     //ユーザーIDでフィルタリング可能

        try {
            Page<SamplingCheckDTO> result = samplingCheckService.getSamplingChecks(page, size, deviceId, userId);    //データの取得を試行
            return ApiResponse.page(
                    result.getContent(),       // 現在のページのデータリスト
                    result.getTotalElements(), // 総件数
                    page,                      // 現在のページ番号
                    size                       // 1ページあたりの件数
            );
        }
        catch (Exception e) {
            return ApiResponse.error(500, "查询失败: " + e.getMessage());    //エラー情報を返却
        }
    }

    //データを新規追加
    @PostMapping
    public ApiResponse<SamplingCheckDTO> addSamplingCheck(@Valid @RequestBody SamplingCheckDTO dto) {
        try{
            return ApiResponse.success("添加成功", samplingCheckService.create(dto));
        }
        catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());      //エラー情報を返却
        }
    }



    //データを更新
    @PutMapping("/{sampling_id}")
    public ApiResponse<SamplingCheckDTO> updateSamplingCheck(
            @PathVariable String sampling_id,             //URLパスからパラメータを取得する
            @Valid @RequestBody SamplingCheckDTO dto) {   //リクエストボディからJSONデータを取得し、DTOに自動でマッピングします。
        try {
            return ApiResponse.success("更新成功", samplingCheckService.update(sampling_id,dto));
        }
        catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    //データを削除
    @DeleteMapping("/{sampling_id}")
    public ApiResponse<Void> deleteSamplingCheck(@PathVariable String sampling_id) {

        try {
            samplingCheckService.delete(sampling_id);                                    //Service層を呼び出してデータを削除する
            return ApiResponse.success("删除成功", null);                      //成功レスポンスを返す
        } catch (RuntimeException e) {
            return ApiResponse.error(404, "删除失败: " + e.getMessage());     //データが存在しません
        }
    }
}
