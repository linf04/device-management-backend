package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DevicePermissionExcelVo;
import com.device.management.dto.DevicePermissionVo;
import com.device.management.dto.PermissionsDTO;
import com.device.management.exception.BusinessException;
import com.device.management.service.DevicePermissionExcelService;
import com.device.management.service.DevicePermissionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    //権限一覧を照会します
    @GetMapping
    public ApiResponse<?> getPermissions(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        if (page < 0 || size < 0) {
            throw new BusinessException(12345, "页码参数错误");
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<DevicePermissionVo> pageResult = devicePermissionService.getPermissions(pageable);
        return ApiResponse.page(pageResult.getContent(), pageResult.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
    }

    //権限を追加します
    @PostMapping
    public ApiResponse<?> addPermissions(@RequestBody PermissionsDTO devicePermission) {
        return ApiResponse.success("権限追加成功", devicePermissionService.addPermissions(devicePermission));
    }

    //権限詳細
    @GetMapping(value = "/{id}")
    public ApiResponse<?> getPermissions(@PathVariable("id") String id) {
        return ApiResponse.success("查询成功", devicePermissionService.getPermissionVoByPermissionId(id));
    }

    //権限を更新します
    @PutMapping(value = "/{id}")
    public ApiResponse<?> updatePermissions(@RequestBody PermissionsDTO permissionsDTO) {
        return ApiResponse.success("更新成功", devicePermissionService.updatePermissions(permissionsDTO));
    }

    //権限を削除します
    @DeleteMapping(value = "/{id}")
    public ApiResponse<?> deletePermissions(@PathVariable("id") String id) {
        return ApiResponse.success("権限削除成功", devicePermissionService.deletePermissions(id));
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