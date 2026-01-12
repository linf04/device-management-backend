package com.device.management.service;

public interface DeviceUsagePermissionService {

    /**
     * IDに基づいてデバイス使用権限を削除
     * @param id 権限ID（APIレイヤーのLong型）
     */
    void deletePermissionById(Long id);
}