package com.device.management.repository;

import com.device.management.entity.DeviceInfo;
import com.device.management.entity.DevicePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface DeviceRepository  extends
        JpaRepository<DeviceInfo, Long>,
        JpaSpecificationExecutor<DeviceInfo>,
        QueryByExampleExecutor<DeviceInfo>
{
    DeviceInfo findByDeviceId(String deviceId);
}
