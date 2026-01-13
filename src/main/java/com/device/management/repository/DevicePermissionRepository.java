package com.device.management.repository;

import com.device.management.dto.DevicePermissionExcelVo;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.DevicePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevicePermissionRepository extends JpaRepository<DevicePermission, String>, JpaSpecificationExecutor<DevicePermission>, QueryByExampleExecutor<DevicePermission> {
    DevicePermission findDevicePermissionsByDevice(DeviceInfo device);

    @Query(value = "SELECT NEW com.device.management.dto.DevicePermissionExcelVo(" + "1L," + "d.deviceId," + "(SELECT STRING_AGG(m.monitorName, CHR(10)) FROM MonitorInfo m WHERE m.device.deviceId = d.deviceId)," + "d.computerName," + " (SELECT STRING_AGG(ip.ipAddress, CHR(10)) FROM DeviceIp ip where ip.device.deviceId = d.deviceId)," + "u.userId," + "u.name," + "u.deptId," + "d.loginUsername," + "dp.domainStatus.id," + "dp.domainGroup," + "dp.noDomainReason," + "dp.smartitStatus.id," + "dp.noSmartitReason," + "dp.usbStatus.id," + "dp.usbReason," + "dp.usbExpireDate," + "dp.antivirusStatus.id," + "dp.noSymantecReason," + "dp.remark" + ")" + "from  User u right join DeviceInfo d on u.userId = d.user.userId " + "left  join DevicePermission dp on   d.deviceId = dp.device.deviceId")
    //エクセルで必要な情報を検索して導き出します
    List<DevicePermissionExcelVo> findAllDevicePermissionExcel();
}
