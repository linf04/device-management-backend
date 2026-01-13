package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DevicePermissionVo {
    private String PermissionId;
    private String deviceId;
    private Object monitorName;
    private String computerName;
    private Object ipAddress;

    // ユーザー情報
    private String UserId;
    private String name;
    private String deptId;
    private String loginUsername;

    // 領域情報
    private Long domainStatusId;
    private String domainGroup;
    private String noDomainReason;

    // SmartIT情報
    private Long smartitStatusId;
    private String noSmartitReason;

    // USB情報
    private Long usbStatusId;
    private String usbReason;
    private LocalDate useExpireDate;

    //その他
    private Long antivirusStatusId;
    private String noSymantecReason;
    private String remark;
}