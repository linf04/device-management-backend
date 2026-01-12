package com.device.management.service.impl;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DictItemDto;
import com.device.management.repository.DictRepository;
import com.device.management.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * &#064;Author:  hexy
 * &#064;Date:  2026/01/05 17:11
 * &#064;Describe:  辞書項目を検索する
 */
@Slf4j
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictRepository dictRepository;
    /**
     * 辞書タイプのコードに基づいて辞書項目を検索する（sortで昇順に並べ替え）
     *
     * @param dictTypeCode 辞書タイプ
     * @return 辞書項目リスト
     */
    @Override
    public ApiResponse<List<DictItemDto>> getDictItemsByTypeCode(String dictTypeCode) {

        try {
            // パラメータを検証する
            if (dictTypeCode == null || dictTypeCode.trim().isEmpty()) {
                return ApiResponse.error(40001, "dictTypeCodeは文字列で指定してください");
            }

            // データベース内の辞書項目を検索する
            List<com.device.management.entity.Dict> dictEntities =
                    dictRepository.findByDictTypeCode(dictTypeCode);

            // 対応する辞書タイプが見つかったか確認する
            if (dictEntities == null || dictEntities.isEmpty()) {
                return ApiResponse.error(40001, "dictTypeCodeは文字列で指定してください");
            }

            // 変換してソートする
            List<DictItemDto> dictItems = dictEntities.stream()
                    .map(dict -> new DictItemDto(
                            dict.getDictId(),
                            dict.getDictItemName(),
                            dict.getSort()
                    ))
                    .sorted(Comparator.comparingInt(DictItemDto::getSort))
                    .collect(Collectors.toList());

            return ApiResponse.success(dictItems);

        } catch (Exception e) {
            log.error("辞書項目を検索中に例外が発生: {}", e.getMessage(), e);
            return ApiResponse.error(50000, "システムエラーが発生しました");
        }
    }
}
