package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dict")
public class Dict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dict_id", columnDefinition = "bigint")
    private Long dictId; // 辞書ID(プライマリキー)

    @Column(name = "dict_type_code", nullable = false, length = 60)
    private String dictTypeCode; // 辞書タイプコード

    @Column(name = "dict_type_name", nullable = false, length = 100)
    private String dictTypeName; // 辞書タイプ名

    @Column(name = "dict_type_description", columnDefinition = "text")
    private String dictTypeDescription; // 辞書タイプ説明

    @Column(name = "dict_item_name", nullable = false, length = 100)
    private String dictItemName; // 辞書項目名

    @Column(name = "sort")
    private Integer sort; // ソート番号

    @Column(name = "is_enabled", nullable = false, columnDefinition = "smallint")
    private Short isEnabled; // 有効フラグ（0=無効、1=有効）

    @Column(name = "create_time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime createTime; // 作成日時

    @Column(name = "creater", length = 100)
    private String creater; // 作成者

    @Column(name = "update_time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime updateTime; // 更新日時

    @Column(name = "updater", length = 100)
    private String updater; // 更新者

    // ========== 列挙型定義 ==========

    /**
     * ディクショナリ種別列挙型
     */
    @Getter
    public enum DictType {
        CONFIRM_STATUS("CONFIRM_STATUS", "本人确认"),
        OS_TYPE("OS_TYPE", "操作系统"),
        MEMORY_SIZE("MEMORY_SIZE", "内存"),
        SSD_SIZE("SSD_SIZE", "固态硬盘"),
        HDD_SIZE("HDD_SIZE", "机械硬盘");

        private final String code;
        private final String name;

        DictType(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    // ========== 業務メソッド ==========

    /**
     * 指定した種別のディクショナリ項目か判定する
     */
    public boolean isType(DictType dictType) {
        return this.dictTypeCode.equals(dictType.getCode());
    }

    /**
     * フル表示名を取得する（種別＋項目名）
     */
    public String getFullDisplayName() {
        return dictTypeName + " - " + dictItemName;
    }
}