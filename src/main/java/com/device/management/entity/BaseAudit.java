package com.device.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class BaseAudit {
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "creater")
    private String creater;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "updater")
    private String updater;
}