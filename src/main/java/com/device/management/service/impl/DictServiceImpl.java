package com.device.management.service.impl;

import com.device.management.dto.ApiResponse;
import com.device.management.common.ApiResponseCode;
import com.device.management.dto.DictItemDTO;
import com.device.management.dto.DictResponse;
import com.device.management.repository.DictRepository;
import com.device.management.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictRepository dictRepository;
    /**
     * 辞書タイプのコードに基づいて辞書項目を検索する（sortで昇順に並べ替え）
     *
     * @param dictTypeCode 辞書タイプ
     * @return 辞書項目リスト
     */
    @Override
    public ApiResponse<DictResponse> getDictItemsByTypeCode(String dictTypeCode) {

        try {
            // パラメータを検証する
            if (dictTypeCode == null || dictTypeCode.trim().isEmpty()) {
                return ApiResponse.error(ApiResponseCode.DICT_PARAM_ERROR, "dictTypeCodeは文字列で指定してください");
            }

            // データベース内の辞書項目を検索する
            List<com.device.management.entity.Dict> dictEntities =
                    dictRepository.findByDictTypeCode(dictTypeCode);

            // 対応する辞書タイプが見つかったか確認する
            if (dictEntities == null || dictEntities.isEmpty()) {
                return ApiResponse.error(ApiResponseCode.DICT_PARAM_ERROR, "dictTypeCodeは文字列で指定してください");
            }

            // 変換してソートする
            List<DictItemDTO> dictItems = dictEntities.stream()
                    .map(dict -> new DictItemDTO(
                            dict.getDictId(),
                            dict.getDictItemName(),
                            dict.getSort()
                    ))
                    .sorted(Comparator.comparingInt(DictItemDTO::getSort))
                    .collect(Collectors.toList());

            DictResponse response= new DictResponse(ApiResponseCode.SUCCESS.getCode(),
                    ApiResponseCode.SUCCESS.getMessage(),dictItems);
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("辞書項目を検索中に例外が発生: {}", e.getMessage(), e);
            return ApiResponse.error(ApiResponseCode.SYSTEM_ERROR, "システムエラーが発生しました");
        }
    }
}
