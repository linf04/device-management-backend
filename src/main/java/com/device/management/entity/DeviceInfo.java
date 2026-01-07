package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "device_info", schema = "public")
public class DeviceInfo{
    @Id
    private String deviceId;

    private String deviceModel;
    private String computerName;
    private String loginUsername;
    private String project;
    private String devRoom;
    private String remark;
    private String userId;
    private Long osId;
    private Long memoryId;
    private Long ssdId;
    private Long hddId;
    private Long selfConfirmId;
    private LocalDateTime createTime;
    private String creater;
    private LocalDateTime updateTime;
    private String updater;
}