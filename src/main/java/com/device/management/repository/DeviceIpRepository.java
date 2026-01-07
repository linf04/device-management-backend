package com.device.management.repository;

import com.device.management.entity.DeviceIp;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface DeviceIpRepository extends JpaRepository<DeviceIp, String>{
    Boolean existsByIpAddress(String ipAddress);

    DeviceIp findByIpAddress(String ipAddress);
    DeviceIp findByIpId(Integer ipId);

}
