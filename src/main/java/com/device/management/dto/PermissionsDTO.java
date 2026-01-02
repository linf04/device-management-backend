package com.device.management.dto;

import com.device.management.entity.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionsDTO {
    private String permissionId;

    private DeviceInfo device;

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
