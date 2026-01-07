package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dict", schema = "public")
public class Dict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dict_id")
    private Long dictId;

    @Column(name = "dict_type_code", length = 50, nullable = false)
    private String dictTypeCode;

    @Column(name = "dict_type_name", length = 100, nullable = false)
    private String dictTypeName;

    @Column(name = "dict_type_description", columnDefinition = "text")
    private String dictTypeDescription;

    @Column(name = "dict_item_name", length = 100, nullable = false)
    private String dictItemName;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "is_enabled", nullable = false)
    private Short isEnabled;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "creater", length = 100)
    private String creater;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "updater", length = 100)
    private String updater;
}