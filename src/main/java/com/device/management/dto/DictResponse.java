package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author： hexy
 * @Date： 2026/01/05 17:42
 * @Describe：処理結果
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictResponse {
    // 処理結果コード
    private Integer token;
    // 処理メッセージ
    private String msg;
    //辞書データ、sort フィールドで昇順ソート済、dictId=value、dictItemName=label
    private List<DictItemDTO> data;
}
