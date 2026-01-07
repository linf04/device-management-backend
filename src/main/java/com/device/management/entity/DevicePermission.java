package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "device_permission", schema = "public")
public class DevicePermission{
    @Id
    private String permissionId;

    private String deviceId;
    private Long domainStatusId;
    private String domainGroup;
    private String noDomainReason;
    private Long smartitStatusId;
    private String noSmartitReason;
    private Long usbStatusId;
    private String usbReason;
    private LocalDate usbExpireDate;
    private Long antivirusStatusId;
    private String noSymantecReason;
    private String remark;
    private LocalDateTime createTime;
    private String creater;
    private LocalDateTime updateTime;
    private String updater;
}