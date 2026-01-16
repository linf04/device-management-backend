package com.device.management.repository;

import com.device.management.entity.DeviceIp;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository
public interface DeviceIpRepository extends JpaRepository<DeviceIp, String>{
    Boolean existsByIpAddress(String ipAddress);

    DeviceIp findByIpAddress(String ipAddress);
    List<DeviceIp> findByDeviceId(String DeviceId);
    DeviceIp findByIpId(Integer ipId);

}
