package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "device_ip", schema = "public")
public class DeviceIp{
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