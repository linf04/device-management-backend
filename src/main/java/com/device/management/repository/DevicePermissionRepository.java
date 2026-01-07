package com.device.management.repository;

import com.device.management.entity.DeviceInfo;
import com.device.management.entity.DevicePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevicePermissionRepository
        extends JpaRepository<DevicePermission, String>, // ID类型是String，不是Long
        JpaSpecificationExecutor<DevicePermission>
{
    DevicePermission findDevicePermissionsByDevice(DeviceInfo device);

    // 通过设备ID查询
    @Query("SELECT dp FROM DevicePermission dp WHERE dp.device.deviceId = :deviceId")
    DevicePermission findByDeviceId(@Param("deviceId") String deviceId);

    // 分页查询
    @Query("SELECT dp FROM DevicePermission dp WHERE dp.device.deviceId = :deviceId")
    Page<DevicePermission> findByDeviceId(@Param("deviceId") String deviceId, Pageable pageable);

    // 通过用户工号查询
    @Query("SELECT dp FROM DevicePermission dp WHERE dp.device.user.userId = :userId")
    Page<DevicePermission> findByUserId(@Param("userId") String userId, Pageable pageable);

    // 同时按设备ID和用户工号查询
    @Query("SELECT dp FROM DevicePermission dp WHERE dp.device.deviceId = :deviceId AND dp.device.user.userId = :userId")
    Page<DevicePermission> findByDeviceIdAndUserId(@Param("deviceId") String deviceId,
                                                      @Param("userId") String userId,
                                                      Pageable pageable);
}