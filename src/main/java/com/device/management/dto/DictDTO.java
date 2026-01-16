package com.device.management.dto;



import com.device.management.entity.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;


//統一辞書DTO

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//辞書項目情報
public class DictDTO {


    private Long dictId;//辞書ID, example = "1"
    private String dictTypeCode;//辞書タイプコード, example = "OS_TYPE"
    private String dictTypeName;//辞書タイプ名, example = "オペレーティングシステム"
    private String dictTypeDescription;//辞書タイプ説明
    private String dictItemName;//辞書項目名, example = "Windows 11"
    private Integer sort;//ソート番号
    private Boolean isEnabled;//有効・無効

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String creater;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String updater;

    private String fullDisplayName;

    /**
     * DictエンティティをDictDTOに変換する
     *
     * @param dict Dictエンティティ
     * @return DictDTO、入力がnullの場合はnullを返す
     */
    public static DictDTO fromEntity(Dict dict) {
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
                .build();
    }

}
