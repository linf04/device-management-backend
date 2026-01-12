package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * &#064;Author: hexy
 * &#064;Data: 2026/01/09
 * &#064;Description: 辞書タイプグループ構造（typeCode + 対応するアイテムリスト）
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictTypeGroup {
    // 辞書タイプコード
    String dictTypeCode;
    // 該当辞書タイプの全てのアイテムリスト
    List<DictItemDto> dictItems;
}
