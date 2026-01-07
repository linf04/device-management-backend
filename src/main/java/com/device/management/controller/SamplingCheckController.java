package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.entity.SamplingCheck;
import com.device.management.mapper.SamplingCheckMapper;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/security-checks")
public class SamplingCheckController {
    @Autowired
    private SamplingCheckService samplingCheckService;

    //xiaoshuang
    //报表导出
    @GetMapping("/export")
    public void exportSamplingChecks(@RequestParam String reportCode,
                                     HttpServletResponse response) throws IOException {
        //创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("工作现场检查");

        //写入表头,表内容
        setTableHead(sheet);
        List<SamplingCheckDTO> samplingCheckDTOS = samplingCheckService.findByReportId(reportCode);
        setTableContent(sheet,1,samplingCheckDTOS);

        // 设置响应头
        String fileName = reportCode + "月度检查表.xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename*=UTF-8''" + encodedFileName );

        // 写入输出流
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    //创建表头
    private void setTableHead(Sheet sheet) {
        Row row0 = sheet.createRow(0);
        Row row1 = sheet.createRow(1);

        // 第一行表头
        row0.createCell(0).setCellValue("编号");
        row0.createCell(1).setCellValue("工号");
        row0.createCell(2).setCellValue("姓名");
        row0.createCell(3).setCellValue("设备编号");
        row0.createCell(4).setCellValue("检查项目");
        row0.createCell(10).setCellValue("处置措施");
        // 第二行表头
        row1.createCell(1).setCellValue("工号");
        row1.createCell(2).setCellValue("姓名");
        row1.createCell(4).setCellValue("开机认证");
        row1.createCell(5).setCellValue("密码屏保");
        row1.createCell(6).setCellValue("安装软件");
        row1.createCell(7).setCellValue("安全补丁");
        row1.createCell(8).setCellValue("病毒防护");
        row1.createCell(9).setCellValue("USB接口(封条)");

        //合并表头
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 9));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 10, 10));
        for (int i = 0; i <= 10; i++) {
            sheet.autoSizeColumn(i);
        }
        log.warn("Writed Table Head ");
    }

    //把数据库记录写入表格
    private void setTableContent(Sheet sheet, int endRow, List<SamplingCheckDTO> samplingCheckDTOS) {
        int rowNum = 0;
        for(SamplingCheckDTO samplingCheckDTO : samplingCheckDTOS) {
            Row row = sheet.createRow( ++endRow );
            row.createCell(0).setCellValue( rowNum++ );
            row.createCell(1).setCellValue( samplingCheckDTO.getUserId());
            row.createCell(2).setCellValue( samplingCheckDTO.getName());
            row.createCell(3).setCellValue( samplingCheckDTO.getDeviceId());
            row.createCell(10).setCellValue( samplingCheckDTO.getDisposalMeasures());
            //通过Option类处理可能为空值的字段
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
        log.warn("Writed Table Content ");
    }



    //wangjunxi
    //获取详细信息
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
    //获取安全检查列表(根据可选条件筛选并对结果进行分页）
    @GetMapping
    public ApiResponse<List<SamplingCheckDTO>> getSamplingChecks(
            @RequestParam int page,                              //获取页数
            @RequestParam(defaultValue = "10") int size,         //每页显示信息条数（默认10条）
            @RequestParam(required = false) String deviceId,     //可以按设备ID进行筛选
            @RequestParam(required = false) String userId) {     //可以按用户ID进行筛选

        try {
            Page<SamplingCheckDTO> result = samplingCheckService.getSamplingChecks(page, size, deviceId, userId);    //尝试获取数据
            return ApiResponse.page(
                    result.getContent(),       // 当前页的数据列表
                    result.getTotalElements(), // 总条数
                    page,                      // 当前页码
                    size                       // 每页大小
            );
        }
        catch (Exception e) {
            return ApiResponse.error(500, "查询失败: " + e.getMessage());    //返回报错信息
        }
    }

    //新增数据
    @PostMapping
    public ApiResponse<SamplingCheckDTO> addSamplingCheck(@Valid @RequestBody SamplingCheckDTO dto) {
        try{
            return ApiResponse.success("添加成功", samplingCheckService.create(dto));
        }
        catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }


    //yuanyue
    //更新数据
    @PutMapping("/{sampling_id}")
    public ApiResponse<SamplingCheckDTO> updateSamplingCheck(
            @PathVariable String sampling_id,             //从URL路径上获取参数
            @Valid @RequestBody SamplingCheckDTO dto) {   //从请求体获取JSON数据，自动装配DTO。
        try {
            return ApiResponse.success("更新成功", samplingCheckService.update(sampling_id,dto));
        }
        catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    //删除数据
    @DeleteMapping("/{sampling_id}")
    public ApiResponse<Void> deleteSamplingCheck(@PathVariable String sampling_id) {

        try {
            samplingCheckService.delete(sampling_id);                                    //调用Service层删除数据
            return ApiResponse.success("删除成功", null);                      //返回成功响应
        } catch (RuntimeException e) {
            return ApiResponse.error(404, "删除失败: " + e.getMessage());     //数据不存在
        }
    }
}
