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

}
