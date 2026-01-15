package com.device.management.repository;

import com.device.management.entity.MonitorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitorInfoRepository extends JpaRepository<MonitorInfo, Integer> {
    
    List<MonitorInfo> findByDeviceId(String deviceId);
}
