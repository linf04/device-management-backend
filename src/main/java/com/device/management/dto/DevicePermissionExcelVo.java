package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
public class DevicePermissionExcelVo {

    private Long number; //エクセル番号
    private String deviceId;
    private Object monitorName;
    private String computerName;
    private Object ipAddress;

    private String userId;
    private String name;
    private String deptId;
    private String loginUsername;

    private Long domainStatusId;
    private String domainGroup;
    private String noDomainReason;

    private Long smartitStatusId;
    private String noSmartitReason;

    private Long usbStatusId;
    private String usbReason;
    private LocalDate useExpireDate;

    private Long antivirusStatusId;
    private String noSymantecReason;
    private String remark;

}