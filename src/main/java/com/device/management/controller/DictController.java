package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DictItemDto;
import com.device.management.dto.DictTypeGroup;
import com.device.management.service.DictService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author： hexy
 * @Date： 2026/01/05 16:56
 * @Describe：辞書項目の取得
 *      GET /api/dict/items
 */

@Slf4j
@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @GetMapping("/items")
    public ApiResponse<List<DictTypeGroup>> getDictItems() {
        return dictService.getDictItems();
    }

}
