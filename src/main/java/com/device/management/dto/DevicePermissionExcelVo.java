package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
public class DevicePermissionExcelVo {

    // 设备信息[java.lang.Long, java.lang.String, java.lang.Object,
    // java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    // java.lang.Long, java.lang.String, java.lang.String, java.lang.Long, java.lang.String,
    // java.lang.Long, java.lang.String,
    // java.time.LocalDate, java.lang.Long, java.lang.String, java.lang.String], and not every argument has an alias)

    private Long number;           // 编号 | 番号
    private String deviceId;      // 设备编号   | 设备ID
    private Object monitorName;     // 显示器   | 显示器名称
    private String computerName;      // 电脑名   | 电脑名称
    private Object ipAddress;         // IP地址（可能有多个）   | IP地址

    private String userId;        // 工号 | 用户ID
    private String name;      // 姓名 | 用户姓名
    private String deptId;    // 部门代码    | 部门ID
    private String loginUsername;     // 登录用户名 | 登录用户名

    private Long domainStatusId;            // 域名 | 域名状态ID
    private String domainGroup;       // 域内组名 | 域内组名称
    private String noDomainReason;    // 不加域理由 | 不加域理由

    private Long smartitStatusId;     // SmartIT状态 | SmartIT状态ID
    private String noSmartitReason;   // 不安装SmartIT理由 | 不安装SmartIT理由

    private Long usbStatusId;         // USB状态 | USB状态ID
    private String usbReason;     // USB开通理由 | USB开通理由
    private LocalDate useExpireDate;         // 使用截止日期 | USB使用截止日期

    private Long antivirusStatusId;  // 连接状态 | 连接状态ID
    private String noSymantecReason;  // 无Symantec理由 | 无Symantec理由
    private String remark;            // 备注 | 备注

}


//    ==================================================================================================
//    select null,d.device_id,d.computer_name,'ip',u.job_number,u.name,u.dept_id,d.login_username,
//dp.domain_status_id,dp.domain_group,dp.no_domain_reason,dp.smartit_status_id,dp.no_smartit_reason,
//dp.usb_status_id,dp.usb_reason,dp.usb_expire_date,dp.antivirus_status_id,dp.no_symantec_reason,dp.remark
//from  users u right join device_info d on u.job_number = d.job_number
//          left  join device_permission dp on   d.device_id = dp.device_id
//    ==================================================================================================