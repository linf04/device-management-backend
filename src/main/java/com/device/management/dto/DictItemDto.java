package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DictItemDto {
    // 辞書ID（下拉框value値）
    private Long dictId;
    // 辞書項目名（下拉框label値）
    private String dictItemName;
    // ソート番号（昇順でソート済）
    private Integer sort;
}