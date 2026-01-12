package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.entity.DeviceUsagePermission;
import com.device.management.exception.ConflictException;
import com.device.management.exception.ResourceNotFoundException;
import com.device.management.repository.DeviceUsagePermissionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceUsagePermissionService {
    @Resource
    private DeviceUsagePermissionRepository permissionRepository;


    @Transactional
    /**
     * IDに基づいてデバイス使用権限を削除
     * @param id 権限ID（APIレイヤーのLong型）
     */

    public void deletePermissionById(String permissionId) {
        // 1. APIのLong型IDをデータベースのString型permissionIdにマッピング
//        permissionId = String.valueOf(permissionId);

        // 2. 権限の存在チェック
        DeviceUsagePermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("権限が存在しません: " + permissionId));

        // 3. 論理削除済みかチェック
        // if (permission.getIsDeleted() != null && permission.getIsDeleted() == 1) {
        //   throw new ResourceNotFoundException("権限は既に削除されています: " + id);
        //}

        // 4. TODO: 関連リソースのチェック（他のテーブルをクエリする必要あり）
        // 模擬：権限IDに"TEST"が含まれている場合、関連があるものとみなす
        if (permissionId.contains("TEST")) {
            throw new ConflictException("権限は既にリソースに紐づいているため、削除できません: " + permissionId);
        }

        // 5. 物理削除の実行
        permissionRepository.delete(permission);
    }
}