package com.device.management.controller;



import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceFullDTO;
import com.device.management.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/insertDevice")
    public ResponseEntity<ApiResponse<DeviceFullDTO>> insertDevice(@RequestBody DeviceFullDTO deviceFullDTO){ //传入creator？

        DeviceFullDTO saveDeviceFullDTO = deviceService.insertDevice(deviceFullDTO); //設備の挿入に成功しました

        return ResponseEntity.ok(ApiResponse.success(saveDeviceFullDTO));
    }

    @PutMapping("/updateDevice/{id}")
    public ResponseEntity<ApiResponse<DeviceFullDTO>> updateDevice(@PathVariable String id, @RequestBody DeviceFullDTO deviceFullDTO){

        DeviceFullDTO updateDeviceFullDTO = deviceService.updateDeviceById(id, deviceFullDTO); //設備の挿入に成功しました

        return ResponseEntity.ok(ApiResponse.success(updateDeviceFullDTO));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        log.info("删除设备，ID: {}", id);

/*        deviceService.deleteDevice(id);*/

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/excel")
    public void exportDevicesToExcel(HttpServletResponse response) {
        log.info("导出设备数据到Excel");
        deviceService.exportDevicesToExcel(response);
    }

    // デバイス一覧取得（ページングとフィルタリング対応）
    // アクセス例：GET /api/devices?userId=JS0105&page=1&size=10
    @GetMapping
    public ApiResponse<Page<DeviceFullDTO>> list(
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(
                deviceService.list(deviceName, userId, page, size)
        );
    }

    // デバイス詳細情報取得
    // アクセス例：GET /api/devices/deviceId
    @GetMapping("/{deviceId}")
    public ApiResponse<DeviceFullDTO> detail(
            @PathVariable String deviceId
    ) {
        return ApiResponse.success(
                deviceService.detail(deviceId)
        );
    }

}
