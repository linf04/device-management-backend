package com.device.management.dto;

import com.device.management.entity.DeviceInfo;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionsDTO {
    private String permissionId;

    private String deviceId;

    private String computerName;

    private String ipAddress;

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
