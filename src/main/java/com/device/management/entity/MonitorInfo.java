package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "monitor_info", schema = "public")
public class MonitorInfo{
    @Id
    private Integer monitorId;

    private String monitorName;
    private String deviceId;
    private LocalDateTime createTime;
    private String creater;
    private LocalDateTime updateTime;
    private String updater;
}