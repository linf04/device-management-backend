package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DevicePermissionExcelVo;
import com.device.management.dto.PermissionInsertDTO;
import com.device.management.dto.PermissionsListDTO;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.User;
import com.device.management.service.DevicePermissionExcelService;
import com.device.management.service.DevicePermissionService;
import com.device.management.service.DeviceUsagePermissionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/permissions")
public class DevicePermissionController {
    @Resource
    DevicePermissionService devicePermissionService;
    @Resource
    private DevicePermissionExcelService devicePermissionExcelService;
    @Resource
    private DeviceUsagePermissionService deviceUsagePermissionService;

    //権限一覧を照会します
    @GetMapping
    public ApiResponse<List<PermissionsListDTO>> getPermissions(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String deviceId) {

        // クエリ条件を構築する
        User user = null;
        if (StringUtils.hasText(userId)) {
            user = new User();
            user.setUserId(userId);
        }

        DeviceInfo deviceInfo = null;
        if (StringUtils.hasText(deviceId)) {
            deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceId(deviceId);
        }

        return devicePermissionService.getPermissions(page, size, user, deviceInfo);
    }

    //権限を追加します
    @PostMapping
    public ApiResponse<?> addPermissions(@RequestBody PermissionInsertDTO devicePermission) {
        return ApiResponse.success("権限追加成功", devicePermissionService.addPermissions(devicePermission));
    }

    //権限をexcelファイル形式でエクスポートします
    @GetMapping(value = "/export")
    public void exportExcel(HttpServletResponse response) throws IOException {
        // データベースからデータを取得する
        List<DevicePermissionExcelVo> dataList = devicePermissionExcelService.getDataFromDatabase();
        // Excelのエクスポート
        devicePermissionExcelService.exportDevicePermissionList(dataList, response);
    }

    /**
     * OASドキュメントで指定された削除インターフェース - パス: /permissions/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<?> deletePermission(@PathVariable("id") String permissionId) {
        deviceUsagePermissionService.deletePermissionById(permissionId);

        return ApiResponse.success("権限削除成功", permissionId);
    }
}
