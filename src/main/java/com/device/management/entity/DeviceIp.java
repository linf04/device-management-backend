package com.device.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "device_ip", schema = "public")
public class DeviceIp {
    @Id
    private Integer ipId;

    @Column(unique = true)
    private String ipAddress;
    private String deviceId;
    private LocalDateTime createTime;
    private String creater;
    private LocalDateTime updateTime;
    private String updater;
}