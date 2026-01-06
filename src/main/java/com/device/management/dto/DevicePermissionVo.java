package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DevicePermissionVo {
    // 设备信息[java.lang.Long, java.lang.String, java.lang.Object,
    // java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    // java.lang.Long, java.lang.String, java.lang.String, java.lang.Long, java.lang.String,
    // java.lang.Long, java.lang.String,
    // java.time.LocalDate, java.lang.Long, java.lang.String, java.lang.String], and not every argument has an alias)
    private String PermissionId;
    private String deviceId;
    private Object monitorName;
    private String computerName;
    private Object ipAddress;

    // 使用者信息
    private String jobNumber;
    private String name;
    private String deptId;
    private String loginUsername;

    // 域信息
    private Long domainStatusId;
    private String domainGroup;
    private String noDomainReason;

    // SmartIT信息
    private Long smartitStatusId;
    private String noSmartitReason;

    // USB信息
    private Long usbStatusId;
    private String usbReason;
    private LocalDate useExpireDate;

    // 其他
    private Long antivirusStatusId;
    private String noSymantecReason;
    private String remark;
}


//    ==================================================================================================
//    select null,d.device_id,d.computer_name,'ip',u.job_number,u.name,u.dept_id,d.login_username,
//dp.domain_status_id,dp.domain_group,dp.no_domain_reason,dp.smartit_status_id,dp.no_smartit_reason,
//dp.usb_status_id,dp.usb_reason,dp.usb_expire_date,dp.antivirus_status_id,dp.no_symantec_reason,dp.remark
//from  users u right join device_info d on u.job_number = d.job_number
//          left  join device_permission dp on   d.device_id = dp.device_id
//    ==================================================================================================