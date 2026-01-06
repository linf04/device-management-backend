
package com.deviceManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dict")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dict {
    @Id
    @Column(name = "dict_id")
    private Long dictId; // 主キー

    @Column(name = "dict_type_code", nullable = false)
    private String dictTypeCode; // 辞書タイプコード

    @Column(name = "dict_type_name", nullable = false)
    private String dictTypeName; // 辞書タイプ名

    @Column(name = "dict_type_description")
    private String dictTypeDescription; // 辞書タイプ説明

    @Column(name = "dict_item_name", nullable = false)
    private String dictItemName; // 辞書項目名

    @Column(name = "sort", columnDefinition = "int4 default 1")
    private Integer sort; // ソート番号

    @Column(name = "is_enabled", nullable = false, columnDefinition = "int2 default 1")
    private Short isEnabled; // 有効フラグ

    @Column(name = "create_time", nullable = false, columnDefinition = "timestamp(6) default CURRENT_TIMESTAMP")
    private LocalDateTime createTime; // 作成時間

    @Column(name = "creater")
    private String creater; // 作成者

    @Column(name = "update_time", nullable = false, columnDefinition = "timestamp(6) default CURRENT_TIMESTAMP")
    private LocalDateTime updateTime; // 更新時間

    @Column(name = "updater")
    private String updater; // 更新者
}