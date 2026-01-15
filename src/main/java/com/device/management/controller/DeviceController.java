package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceFullDTO;
import com.device.management.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /* 4. 导出设备到Excel */
    @GetMapping("/export/excel")
    public void exportDevicesToExcel(HttpServletResponse response) {
        log.info("导出设备数据到Excel");
        deviceService.exportDevicesToExcel(response);
    }

    /* 5. 获取设备列表（分页和过滤） */
    @GetMapping
    public ApiResponse<Page<DeviceFullDTO>> listDevices(
            @RequestParam(required = false) String computerName,  // 修改参数名
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("查询设备列表: computerName={}, userId={}, page={}, size={}",
                computerName, userId, page, size);
        Page<DeviceFullDTO> result = deviceService.list(computerName, userId, page, size);

        // 使用 Page 对象创建 ApiResponse
        ApiResponse<Page<DeviceFullDTO>> response = new ApiResponse<>(200, "查询成功", result);
        response.setTotal(result.getTotalElements());
        response.setPage(page);
        response.setSize(size);

        log.info("【调试】原始参数: computerName='{}', userId='{}', 是否空: {}",
                computerName, userId, !StringUtils.hasText(computerName) && !StringUtils.hasText(userId));

        return response;
    }

    /* 1. 新增设备 */
    @PostMapping
    public ApiResponse<DeviceFullDTO> insertDevice(@RequestBody DeviceFullDTO deviceFullDTO) {
        log.info("新增设备请求: {}", deviceFullDTO.getDeviceId());
        return deviceService.insertDevice(deviceFullDTO);
    }

    /* 6. 获取设备详情 */
    @GetMapping("/{deviceId}")
    public ApiResponse<DeviceFullDTO> getDeviceDetail(@PathVariable String deviceId) {
        log.info("查询设备详情: {}", deviceId);
        return deviceService.detail(deviceId);
    }

    /* 2. 更新设备 */
    @PutMapping("/{deviceId}")
    public ApiResponse<DeviceFullDTO> updateDevice(
            @PathVariable String deviceId,
            @RequestBody DeviceFullDTO deviceFullDTO) {
        log.info("更新设备请求: {}", deviceId);
        return deviceService.updateDeviceById(deviceId, deviceFullDTO);
    }

    /* 3. 删除设备 */
    @DeleteMapping("/{deviceId}")
    public ApiResponse<String> deleteDevice(@PathVariable String deviceId) {
        log.info("删除设备请求: {}", deviceId);
        return deviceService.deleteDevice(deviceId);
    }


}
