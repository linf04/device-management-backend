package com.device.management.controller;

import com.device.management.dto.*;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.User;
import com.device.management.service.DevicePermissionExcelService;
import com.device.management.service.DevicePermissionService;
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
        return devicePermissionService.addPermissions(devicePermission);
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
        devicePermissionService.deletePermissionById(permissionId);
        return ApiResponse.success("権限削除成功", permissionId);
    }
    /**
     * 権限IDに基づいて詳細情報を取得
     * リクエスト方式：GET
     * リクエストパス：/permissions/{permissionId}
     * 1. パスパラメータ permissionId を受け取る
     * 2. サービス層の findPermissionDetail メソッドを呼び出す
     * 3. サービス層がデータベースからデータを取得し、DTO に変換する
     * 4. APIレスポンスとして統一された ApiResponse にパッケージ化して返す
     */
    @GetMapping("/{permissionId}")
    public ApiResponse<?> getPermissionDetail(@PathVariable String permissionId) {
        //サービス層を呼び出してデータを取得する
        return  devicePermissionService.findPermissionDetail(permissionId);
    }

    /**
     * 権限情報の更新
     * リクエスト方式：PUT
     * リクエストパス：/permissions/{permissionId}
     * 1. パスパラメータ permissionId を受け取る
     * 2. RequestBodyから更新リクエストオブジェクトを受け取る
     * 3. サービス層の updatePermissionByFields メソッドを呼び出す
     * 4. 操作結果を返す
     */
    @PutMapping("/{permissionId}")
    public ApiResponse<?> updatePermission(
            //更新する権限ID
            @PathVariable String permissionId,
            @RequestBody PermissionUpdateDTO updateDTO) {

        return devicePermissionService.updatePermissionByFields(permissionId, updateDTO);
    }
}
