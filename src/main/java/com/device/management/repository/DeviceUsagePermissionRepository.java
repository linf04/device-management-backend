package com.device.management.repository;

import com.device.management.entity.DeviceUsagePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceUsagePermissionRepository 
    extends JpaRepository<DeviceUsagePermission, String> {
    
    // 権限IDで検索
    DeviceUsagePermission findByPermissionId(String permissionId);
    
    // 権限が存在するか確認する
    boolean existsByPermissionId(String permissionId);
}