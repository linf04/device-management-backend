package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.PermissionsDTO;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.User;
import com.device.management.service.DevicePermissionService;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*功能	方法	端点	参数/请求体	说明
列表	GET	/permissions	?page=1&size=10&deviceId=xxx&userId=xxx	-
详情	GET	/permissions/{id}	-	-
新增	POST	/permissions	Permission 对象	-
编辑	PUT	/permissions/{id}	Permission 对象	-
导出	GET	/permissions/export	-	返回 Excel 文件
删除 DELETE	/permissions/{id}	-	-
*/

@RestController
@RequestMapping("/permissions")
public class DevicePermissionController {
    @Resource
    DevicePermissionService devicePermissionService;

    //権限一覧を照会します
    @GetMapping
    public ApiResponse<List<PermissionsDTO>> getPermissions(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String deviceId) {

        // 构建查询条件
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
    public  ApiResponse<PermissionsDTO> addPermissions(
            @RequestBody PermissionsDTO devicePermission
    ){
        return devicePermissionService.addPermissions(devicePermission);
    }

    //権限詳細
    @GetMapping(value = "/{id}")
    public ApiResponse<PermissionsDTO> getPermissions() {
        return null;
    }

    //権限を更新します
    @PutMapping
    public ApiResponse<PermissionsDTO> updatePermissions(
            @RequestBody PermissionsDTO permissionsDTO
    ){
        return devicePermissionService.updatePermissions(permissionsDTO);
    }

    //権限を削除します
    @DeleteMapping(value = "/{id}")
    public ApiResponse<Void> deletePermissions(
            @PathVariable("id") String id
    ){
        return devicePermissionService.deletePermissions(id);
    }

    //権限をexcelファイル形式でエクスポートします
    @GetMapping(value = "/export")
    public ApiResponse<Void> exportPermissions(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "userInfo", required = false) User user,
            @RequestParam(value = "deviceInfo", required = false) DeviceInfo deviceInfo,
            @RequestParam(value = "permissionInfo", required = false) String permissionInfo
    )
    {
        return devicePermissionService.exportPermissions(page,size,user,deviceInfo,permissionInfo);
    }
}
