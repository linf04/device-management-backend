package com.device.management.repository;


import com.device.management.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/*
* 设备 Repository
* */
@Repository
public interface  DeviceRepository extends JpaRepository<Device, String> {
    // 根据用户ID查找设备
    List<Device> findByUserId(String userId);

    // 根据开发室查找设备
    List<Device> findByDevRoom(String devRoom);

    // 根据项目名称查找设备
    List<Device> findByProject(String project);

}
