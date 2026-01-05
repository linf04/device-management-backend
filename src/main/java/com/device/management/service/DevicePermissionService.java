package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.PermissionsDTO;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevicePermissionService {

    public ApiResponse<List<PermissionsDTO>> getPermissions(Integer page, Integer size, User user, DeviceInfo deviceInfo, String permissionInfo) {
        return null;
    }

    public ApiResponse<PermissionsDTO> addPermissions(PermissionsDTO permissionsDTO) {
        return null;
    }

    public ApiResponse<PermissionsDTO> updatePermissions(PermissionsDTO permissionsDTO) {
        return null;
    }

    public ApiResponse<Void> deletePermissions(String id) {
        return null;
    }

    public ApiResponse<Void> exportPermissions(Integer page, Integer size, User user, DeviceInfo deviceInfo, String permissionInfo) {
        return null;
    }
}
