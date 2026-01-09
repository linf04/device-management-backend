package com.device.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "dict")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dict_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "dict_type_code", nullable = false, length = 50)
    private String dictTypeCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "dict_type_name", nullable = false, length = 100)
    private String dictTypeName;

    @Column(name = "dict_type_description", length = Integer.MAX_VALUE)
    private String dictTypeDescription;

    @Size(max = 100)
    @NotNull
    @Column(name = "dict_item_name", nullable = false, length = 100)
    private String dictItemName;

    @ColumnDefault("0")
    @Column(name = "sort")
    private Integer sort;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "is_enabled", nullable = false)
    private Short isEnabled;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @Size(max = 100)
    @Column(name = "creater", length = 100)
    private String creater;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    @Size(max = 100)
    @Column(name = "updater", length = 100)
    private String updater;


}