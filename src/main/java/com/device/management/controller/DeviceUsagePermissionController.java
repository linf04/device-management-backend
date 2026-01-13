package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceUsagePermissionDTO;
import com.device.management.service.DeviceUsagePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/permissions")
public class DeviceUsagePermissionController {
    @Autowired
    private DeviceUsagePermissionService permissionService;

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
    public ApiResponse<DeviceUsagePermissionDTO> getPermissionDetail(@PathVariable String permissionId) {
        //サービス層を呼び出してデータを取得する
        DeviceUsagePermissionDTO dto = permissionService.findPermissionDetail(permissionId);
        //統一されたレスポンス形式をカプセル化して返す
        return ApiResponse.success("検索に成功しました", dto);
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
    public ApiResponse<Void> updatePermission(
            //更新する権限ID
            @PathVariable String permissionId,
            @RequestBody DeviceUsagePermissionDTO updateDTO) {

        permissionService.updatePermissionByFields(permissionId, updateDTO);
        //成功レスポンスを返す
        return ApiResponse.success("更新に成功しました");
    }
}