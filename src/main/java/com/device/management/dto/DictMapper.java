package com.device.management.dto;

import com.device.management.entity.Dict;

public class DictMapper {

    /**
     * DictエンティティをDictDTOに変換する
     *
     * @param dict Dictエンティティ
     * @return DictDTO、入力がnullの場合はnullを返す
     */
    public static DictDTO toDTO(Dict dict) {
        if (dict == null) {
            return null;
        }

        return DictDTO.builder()
                .dictId(dict.getDictId())
                .dictTypeCode(dict.getDictTypeCode())
                .dictTypeName(dict.getDictTypeName())
                .dictTypeDescription(dict.getDictTypeDescription())
                .dictItemName(dict.getDictItemName())
                .sort(dict.getSort())
                // Short型をBoolean型に変換 (1: true, その他: false)
                .isEnabled(dict.getIsEnabled() != null && dict.getIsEnabled() == 1)
                .createTime(dict.getCreateTime())
                .creater(dict.getCreater())
                .updateTime(dict.getUpdateTime())
                .updater(dict.getUpdater())
                .fullDisplayName(dict.getFullDisplayName())
                .build();
    }
}