package com.device.management.repository;

import com.device.management.dto.DevicePermissionExcelVo;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.DevicePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DevicePermissionRepository extends JpaRepository<DevicePermission, String>, JpaSpecificationExecutor<DevicePermission>, QueryByExampleExecutor<DevicePermission> {
    DevicePermission findDevicePermissionsByDevice(DeviceInfo device);

    @Query(value = "SELECT NEW com.device.management.dto.DevicePermissionExcelVo(" + "1L," + "d.deviceId," + "(SELECT STRING_AGG(m.monitorName, CHR(10)) FROM MonitorInfo m WHERE m.device.deviceId = d.deviceId)," + "d.computerName," + " (SELECT STRING_AGG(ip.ipAddress, CHR(10)) FROM DeviceIp ip where ip.device.deviceId = d.deviceId)," + "u.userId," + "u.name," + "u.deptId," + "d.loginUsername," + "dp.domainStatus.id," + "dp.domainGroup," + "dp.noDomainReason," + "dp.smartitStatus.id," + "dp.noSmartitReason," + "dp.usbStatus.id," + "dp.usbReason," + "dp.usbExpireDate," + "dp.antivirusStatus.id," + "dp.noSymantecReason," + "dp.remark" + ")" + "from  User u right join DeviceInfo d on u.userId = d.user.userId " + "left  join DevicePermission dp on   d.deviceId = dp.device.deviceId")
    //エクセルで必要な情報を検索して導き出します
    List<DevicePermissionExcelVo> findAllDevicePermissionExcel();

    //デバイスIDで検索
//    List<DevicePermission> findByDeviceId(String deviceId);

    // 特定の権限IDが存在するか確認する
//    boolean existsByPermissionId(String permissionId);

    //permission_idに基づいて単一の権限を検索する
    @Query("SELECT dp FROM DevicePermission dp WHERE dp.permissionId = :permissionId")
    DevicePermission findByPermissionId(@Param("permissionId") String permissionId);

    // permission_idに基づいて削除（@Modifyingと@Transactionalが必要）
    @Modifying
    @Transactional
    @Query("DELETE FROM DevicePermission dp WHERE dp.permissionId = :permissionId")
    int deleteByPermissionId(@Param("permissionId") String permissionId);
}
