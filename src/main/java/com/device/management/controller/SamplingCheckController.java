package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.SamplingCheckDTO;
import com.device.management.service.SamplingCheckService;
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
