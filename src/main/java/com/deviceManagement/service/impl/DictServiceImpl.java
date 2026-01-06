package com.deviceManagement.service.impl;

import com.deviceManagement.common.Result;
import com.deviceManagement.common.ResultCode;
import com.deviceManagement.dto.DictItemDTO;
import com.deviceManagement.dto.DictResponse;
import com.deviceManagement.repository.DictRepository;
import com.deviceManagement.service.DictService;
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
    public Result<DictResponse> getDictItemsByTypeCode(String dictTypeCode) {

        try {
            // パラメータを検証する
            if (dictTypeCode == null || dictTypeCode.trim().isEmpty()) {
                return Result.error(ResultCode.DICT_PARAM_ERROR, "dictTypeCodeは文字列で指定してください");
            }

            // データベース内の辞書項目を検索する
            List<com.deviceManagement.entity.Dict> dictEntities =
                    dictRepository.findByDictTypeCode(dictTypeCode);

            // 対応する辞書タイプが見つかったか確認する
            if (dictEntities == null || dictEntities.isEmpty()) {
                return Result.error(ResultCode.DICT_PARAM_ERROR, "dictTypeCodeは文字列で指定してください");
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

            DictResponse response= new DictResponse(ResultCode.SUCCESS.getCode(),
                    ResultCode.SUCCESS.getMessage(),dictItems);
            return Result.success(response);

        } catch (Exception e) {
            log.error("辞書項目を検索中に例外が発生: {}", e.getMessage(), e);
            return Result.error(ResultCode.SYSTEM_ERROR, "システムエラーが発生しました");
        }
    }

    /*    @Override
    public Result<DictResponse> getDictItemsByTypeCode(String dictTypeCode) {
        return Result.success(new DictResponse(ResultCode.SUCCESS.getCode(),
                        ResultCode.SUCCESS.getMessage(),List.of(new DictItemDTO(1L,"使用可",1),new DictItemDTO(2L,
                        "使用不可",2))));
    }
    */

}
