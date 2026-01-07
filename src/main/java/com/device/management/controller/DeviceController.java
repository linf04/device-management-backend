package com.device.management.controller;



import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceDTO;
import com.device.management.dto.DeviceFullDTO;
import com.device.management.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test(){

        return ResponseEntity.ok(ApiResponse.success("getテスト成功"));
    }


    @PostMapping("/insertDevice")
    public ResponseEntity<ApiResponse<DeviceFullDTO>> insertDevice(@RequestBody DeviceFullDTO deviceFullDTO){ //传入creator？

        DeviceFullDTO deviceFullDTO1 = deviceService.insertDevice(deviceFullDTO); //設備の挿入に成功しました

        return ResponseEntity.ok(ApiResponse.success(deviceFullDTO1));
    }



//    @PostMapping("/insertDevice")
//    public ResponseEntity<ApiResponse<DeviceDTO>> insertDevice(@RequestBody DeviceDTO deviceDTO){ //传入creator？
//
//        DeviceDTO deviceDTO1 = deviceService.insertDevice(deviceDTO); //設備の挿入に成功しました
//
//        return ResponseEntity.ok(ApiResponse.success(deviceDTO1));
//    }

    @PutMapping("/updateDevice/{id}")
    public ResponseEntity<ApiResponse<DeviceFullDTO>> updateDevice(@PathVariable String id, @RequestBody DeviceFullDTO deviceFullDTO){

        DeviceFullDTO deviceFullDTO1 = deviceService.updateDeviceById(id, deviceFullDTO); //設備の挿入に成功しました

        return ResponseEntity.ok(ApiResponse.success(deviceFullDTO1));

    }

//    @PutMapping("/updateDevice/{id}")
//    public ResponseEntity<ApiResponse<DeviceDTO>> updateDevice(@PathVariable String id, @RequestBody DeviceDTO deviceDTO){
//
//        DeviceDTO deviceDTO1 = deviceService.updateDeviceById(id, deviceDTO); //設備の挿入に成功しました
//
//        return ResponseEntity.ok(ApiResponse.success(deviceDTO1));
//
//    }

import com.device.management.service.DeviceService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("测试端点被访问");
        return ResponseEntity.ok("测试成功");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        log.info("删除设备，ID: {}", id);
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/excel")
    public void exportDevicesToExcel(HttpServletResponse response) {
        log.info("导出设备数据到Excel");
        deviceService.exportDevicesToExcel(response);
    }

}
