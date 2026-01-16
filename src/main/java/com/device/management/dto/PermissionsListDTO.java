package com.device.management.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionsListDTO {
    private String permissionId;
    private String deviceId;
    private List<String> monitorNames;
    private String computerName;
    private List<String> ipAddress;
    private String userId;
    private String name;
    private String deptId;
    private String loginUsername;
    private Long domainStatus;
    private String domainGroup;
    private String noDomainReason;
    private Long smartitStatus;
    private String noSmartitReason;
    private Long usbStatus;
    private String usbReason;
    private LocalDate usbExpireDate;
    private Long antivirusStatus;
    private String noSymantecReason;
    private String remark;
    private LocalDateTime createTime;
    private String creater;
    private LocalDateTime updateTime;
    private String updater;
}