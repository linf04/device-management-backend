package com.device.management.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionsDTO {
    private String permissionId;
    private String deviceId;
    private List<String> monitorNames;
    private String computerName;
    private List<String> ipAddress;
    private String userId;
    private String name;
    private String deptId;
    private String loginUsername;
    private int domainStatus;
    private String domainGroup;
    private String noDomainReason;
    private int smartitStatus;
    private String noSmartitReason;
    private int usbStatus;
    private String usbReason;
    private LocalDate usbExpireDate;
    private int antivirusStatus;
    private String noSymantecReason;
    private String remark;
    private Instant createTime;
    private String creater;
    private Instant updateTime;
    private String updater;
}