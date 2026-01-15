package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceFullDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

public interface DeviceService {
    /**
     * 新增设备
     */
    ApiResponse<DeviceFullDTO> insertDevice(DeviceFullDTO deviceFullDTO);

    /**
     * 更新设备
     */
    ApiResponse<DeviceFullDTO> updateDeviceById(String deviceId, DeviceFullDTO deviceFullDTO);

    /**
     * 删除设备
     */
    ApiResponse<String> deleteDevice(String deviceId);

    /**
     * 导出设备到Excel
     */
    void exportDevicesToExcel(HttpServletResponse response);

    /**
     * 获取设备列表（分页和过滤）
     */
    Page<DeviceFullDTO> list(String computerName, String userId, int page, int size);

    /**
     * 获取设备详情
     */
    ApiResponse<DeviceFullDTO> detail(String deviceId);
}