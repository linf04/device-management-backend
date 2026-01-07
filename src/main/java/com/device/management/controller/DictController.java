package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DictResponse;
import com.device.management.service.DictService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Author： hexy
 * @Date： 2026/01/05 16:56
 * @Describe：辞書項目の取得
 *      GET /api/dict/items
 */

@Slf4j
@RestController
@RequestMapping("/dict")
@RequiredArgsConstructor
public class DictController {

    @Resource
    private final DictService dictService;

    @GetMapping("/items")
    public ApiResponse<DictResponse> getDictItemsByTypeCode(@RequestParam String dictTypeCode) {
        return dictService.getDictItemsByTypeCode(dictTypeCode);
    }

}
