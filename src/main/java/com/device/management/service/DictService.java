package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DictItemDto;
import com.device.management.dto.DictTypeGroup;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author： hexy
 * @Date： 2026/01/05 17:04
 * @Describe： 辞書管理サービスインターフェース
 */
@Service
public interface DictService {
    /** 辞書タイプのコードに基づいて辞書項目を検索する（sortで昇順に並べ替え）
    * @return 辞書項目リスト
    */
    ApiResponse<List<DictTypeGroup>> getDictItems();
}
