package com.device.management.repository;

import com.device.management.entity.Monitor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MonitorRepository extends JpaRepository<Monitor, String>{
    Boolean existsByMonitorName(String monitorName);
//    Monitor findByDeviceId(String DeviceId);
    List<Monitor> findByDeviceId(String DeviceId);
    Monitor findByMonitorName(String monitorName);
    Monitor findByMonitorId(Integer monitorId);
}
