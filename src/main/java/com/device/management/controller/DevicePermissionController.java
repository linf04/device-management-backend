package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DevicePermissionExcelVo;
import com.device.management.dto.PermissionInsertDTO;
import com.device.management.service.DevicePermissionExcelService;
import com.device.management.service.DevicePermissionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @Resource
    private DevicePermissionExcelService devicePermissionExcelService;

    //権限を追加します
    @PostMapping
    public ApiResponse<?> addPermissions(@RequestBody PermissionInsertDTO devicePermission) {
        return ApiResponse.success("権限追加成功", devicePermissionService.addPermissions(devicePermission));
    }

    //権限をexcelファイル形式でエクスポートします
    @GetMapping(value = "/export")
    public void exportExcel(HttpServletResponse response) throws IOException {
        // 这里应该从数据库获取数据
        List<DevicePermissionExcelVo> dataList = devicePermissionExcelService.getDataFromDatabase();
        // 导出Excel
        devicePermissionExcelService.exportDevicePermissionList(dataList, response);
    }
}