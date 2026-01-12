package com.device.management.service.impl;

import com.device.management.entity.DeviceUsagePermission;
import com.device.management.exception.ConflictException;
import com.device.management.exception.ResourceNotFoundException;
import com.device.management.repository.DeviceUsagePermissionRepository;
import com.device.management.service.DeviceUsagePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceUsagePermissionServiceImpl implements DeviceUsagePermissionService {

    @Autowired
    private DeviceUsagePermissionRepository permissionRepository;

    @Override
    @Transactional
    public void deletePermissionById(Long id) {
        System.out.println("デバイス使用権限の削除開始: id=" + id);

        // 1. APIのLong型IDをデータベースのString型permissionIdにマッピング
        String permissionId = String.valueOf(id);

        // 2. 権限の存在チェック
        DeviceUsagePermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("権限が存在しません: " + id));

        // 3. 論理削除済みかチェック
        // if (permission.getIsDeleted() != null && permission.getIsDeleted() == 1) {
        //   throw new ResourceNotFoundException("権限は既に削除されています: " + id);
        //}

        // 4. TODO: 関連リソースのチェック（他のテーブルをクエリする必要あり）
        // 模擬：権限IDに"TEST"が含まれている場合、関連があるものとみなす
        if (permissionId.contains("TEST")) {
            throw new ConflictException("権限は既にリソースに紐づいているため、削除できません: " + id);
        }

        // 5. 物理削除の実行
        permissionRepository.delete(permission);

        System.out.println("権限の物理削除に成功しました: id=" + id + ", permissionId=" + permissionId);
    }
}