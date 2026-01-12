package com.device.management.service.impl;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DictItemDto;
import com.device.management.dto.DictTypeGroup;
import com.device.management.entity.Dict;
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
     * @return 辞書項目リスト
     */
    @Override
    public ApiResponse<List<DictTypeGroup>> getDictItems() {

        try {
            // データベース内の辞書項目を検索する
            List<com.device.management.entity.Dict> dictEntities =
                    dictRepository.findAll();

            // 核心手順：dictTypeCodeごとにグループ化 → グループ内で処理 → DictTypeGroupとしてパッケージ化
            List<DictTypeGroup> dictTypeGroups = dictEntities.stream()
                    // 第1ステップ：dictTypeCodeで分割（グループ化）、Map<dictTypeCode, List<Dict>>を取得
                    .collect(Collectors.groupingBy(Dict::getDictTypeCode))
                    // 第2ステップ：MapのentrySetを走査（key=dictTypeCode、value=該当タイプのすべてのDict）
                    .entrySet().stream()
                    // 第3ステップ：各グループをDictTypeGroupとしてラップ
                    .map(entry -> {
                        // グループ内処理：Dict → DictItemDto + sort昇順でソート
                        List<DictItemDto> dictItemList = entry.getValue().stream()
                                // DictItemDto に変換
                                .map(dict -> new DictItemDto(
                                        dict.getDictId(),
                                        dict.getDictItemName(),
                                        dict.getSort()
                                ))
                                // グループ内でsort昇順にソート（重要：同一dictTypeCodeのデータのみソート）
                                .sorted(Comparator.comparingInt(DictItemDto::getSort))
                                .collect(Collectors.toList());

                        // 現在のグループの DictTypeGroupをカプセル化
                        DictTypeGroup dictTypeGroup = new DictTypeGroup();
                        dictTypeGroup.setDictTypeCode(entry.getKey()); // グループの dictTypeCodeをバインド
                        dictTypeGroup.setDictItems(dictItemList); // グループ内処理後の辞書項目リストをバインド
                        return dictTypeGroup;
                    })
                    // 全てのグループをList<DictTypeGroup>として収集
                    .collect(Collectors.toList());


            return ApiResponse.success(dictTypeGroups);

        } catch (Exception e) {
            log.error("辞書項目を検索中に例外が発生: {}", e.getMessage(), e);
            return ApiResponse.error(50000, "システムエラーが発生しました");
        }
    }
}
