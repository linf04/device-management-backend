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

    private Instant createTime;

    private String creater;

    private Instant updateTime;

    private String updater;
}
