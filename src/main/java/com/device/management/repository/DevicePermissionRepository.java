package com.device.management.repository;

import com.device.management.dto.DevicePermissionExcelVo;
import com.device.management.dto.DevicePermissionVo;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.DevicePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevicePermissionRepository extends JpaRepository<DevicePermission, Long>, JpaSpecificationExecutor<DevicePermission>, QueryByExampleExecutor<DevicePermission> {
    DevicePermission findDevicePermissionsByDevice(DeviceInfo device);

    @Query(value = "SELECT NEW com.device.management.dto.DevicePermissionExcelVo(" + "1L," + "d.deviceId," + "(SELECT STRING_AGG(m.monitorName, CHR(10)) FROM MonitorInfo m WHERE m.device.deviceId = d.deviceId)," + "d.computerName," + " (SELECT STRING_AGG(ip.ipAddress, CHR(10)) FROM DeviceIp ip where ip.device.deviceId = d.deviceId)," + "u.jobNumber," + "u.name," + "u.deptId," + "d.loginUsername," + "dp.domainStatus.id," + "dp.domainGroup," + "dp.noDomainReason," + "dp.smartitStatus.id," + "dp.noSmartitReason," + "dp.usbStatus.id," + "dp.usbReason," + "dp.usbExpireDate," + "dp.antivirusStatus.id," + "dp.noSymantecReason," + "dp.remark" + ")" + "from  User u right join DeviceInfo d on u.jobNumber = d.jobNumber.jobNumber " + "left  join DevicePermission dp on   d.deviceId = dp.device.deviceId")
    List<DevicePermissionExcelVo> findAllDevicePermissionExcel();

    @Query(value = "SELECT NEW com.device.management.dto.DevicePermissionVo(" + "dp.permissionId," + "d.deviceId," + "(SELECT STRING_AGG(m.monitorName, CHR(10)) FROM MonitorInfo m WHERE m.device.deviceId = d.deviceId)," + "d.computerName," + " (SELECT STRING_AGG(ip.ipAddress, CHR(10)) FROM DeviceIp ip where ip.device.deviceId = d.deviceId)," + "u.jobNumber," + "u.name," + "u.deptId," + "d.loginUsername," + "dp.domainStatus.id," + "dp.domainGroup," + "dp.noDomainReason," + "dp.smartitStatus.id," + "dp.noSmartitReason," + "dp.usbStatus.id," + "dp.usbReason," + "dp.usbExpireDate," + "dp.antivirusStatus.id," + "dp.noSymantecReason," + "dp.remark" + ")" + "from  User u right join DeviceInfo d on u.jobNumber = d.jobNumber.jobNumber " + "left  join DevicePermission dp on   d.deviceId = dp.device.deviceId")
    Page<DevicePermissionVo> findAllDevicePermissionVo(Pageable pageable);

    @Query(value = "SELECT NEW com.device.management.dto.DevicePermissionVo(" + "dp.permissionId," + "d.deviceId," + "(SELECT STRING_AGG(m.monitorName, CHR(10)) FROM MonitorInfo m WHERE m.device.deviceId = d.deviceId)," + "d.computerName," + " (SELECT STRING_AGG(ip.ipAddress, CHR(10)) FROM DeviceIp ip where ip.device.deviceId = d.deviceId)," + "u.jobNumber," + "u.name," + "u.deptId," + "d.loginUsername," + "dp.domainStatus.id," + "dp.domainGroup," + "dp.noDomainReason," + "dp.smartitStatus.id," + "dp.noSmartitReason," + "dp.usbStatus.id," + "dp.usbReason," + "dp.usbExpireDate," + "dp.antivirusStatus.id," + "dp.noSymantecReason," + "dp.remark" + ")" + "from  User u right join DeviceInfo d on u.jobNumber = d.jobNumber.jobNumber " + "left  join DevicePermission dp on   d.deviceId = dp.device.deviceId" + " where dp.permissionId = ?1")
    DevicePermissionVo findPermissionVoByPermissionId(String permissionId);

    DevicePermission findDevicePermissionByPermissionId(String permissionId);

}

//public class PermissionsDTO {
//    private String permissionId;

//    private String deviceId;
//
//    private int domainStatus;
//
//    private String domainGroup;
//
//    private String noDomainReason;
//
//    private int smartitStatus;
//
//    private String noSmartitReason;
//
//    private int usbStatus;
//
//    private String usbReason;
//
//    private LocalDate usbExpireDate;
//
//    private int antivirusStatus;
//
//    private String noSymantecReason;
//
//    private String remark;
//
//    private Instant createTime;
//
//    private String creater;
//
//    private Instant updateTime;
//
//    private String updater;
//}

/*
 * user（0.5）
 * device（1）
 * mon（s） ip（s） devicePer（1）
 * */

/*
public class DevicePermissionExcelVo {
    // 设备信息
    private Long number;           // 编号
    private String deviceNumber;      // 设备编号（带显示器信息）
    private String computerName;      // 电脑名
    private String ipAddress;         // IP地址（可能有多个）
    private String monitorName;     // 显示器

    // 使用者信息
    private String employeeId;        // 工号
    private String employeeName;      // 姓名
    private String departmentCode;    // 部门代码
    private String loginUsername;     // 登录用户名

    // 域信息
    private Long domain;            // 域名
    private String domainGroup;       // 域内组名
    private String noDomainReason;    // 不加域理由

    // SmartIT信息
    private Long smartitStatus;     // SmartIT状态
    private String noSmartitReason;   // 不安装SmartIT理由

    // USB信息
    private Long usbStatus;         // USB状态
    private String usbOpenReason;     // USB开通理由
    private Date useDeadline;         // 使用截止日期

    // 其他
    private Long connectionStatus;  // 连接状态
    private String noSymantecReason;  // 无Symantec理由
    private String remark;            // 备注

[java.lang.Long, java.lang.String, java.lang.String, java.lang.String,
 java.lang.String, java.lang.String, java.lang.String, java.lang.Long,
 java.lang.String, java.lang.String, java.lang.Long, java.lang.String,
 java.lang.Long, java.lang.String, java.time.LocalDate, java.lang.Long,
 java.lang.String, java.lang.String]
}*/


