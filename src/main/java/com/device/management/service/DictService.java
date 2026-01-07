package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DictResponse;

/**
 * @Author： hexy
 * @Date： 2026/01/05 17:04
 * @Describe： 辞書管理サービスインターフェース
 */

public interface DictService {
    /** 辞書タイプのコードに基づいて辞書項目を検索する（sortで昇順に並べ替え）
    * @param dictTypeCode
    * @return 辞書項目リスト
    */
    ApiResponse<DictResponse> getDictItemsByTypeCode(String dictTypeCode);
}
