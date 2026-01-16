package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceFullDTO;
import com.device.management.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * デバイス管理コントローラー
 *  * デバイスの追加・削除・編集・検索機能を提供
 *
 * @author device-management
 */
@Slf4j
@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * デバイス一覧を取得（ページングとフィルタリング）
     * アクセス例：GET /devices?deviceName=xxx&userId=JS0105&page=1&size=10
     *
     * @param userId     ユーザーID（オプション）
     * @param page       ページ番号、デフォルト1
     * @param size       1ページあたりの件数、デフォルト10
     * @return ページングされたデバイス一覧
     */
    @GetMapping
    public ApiResponse<List<DeviceFullDTO>> listDevices(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("デバイス一覧を照会: userId={}, page={}, size={}", userId, page, size);
        Page<DeviceFullDTO> result = deviceService.list(userId, page, size);

        return ApiResponse.page(result.getContent(), result.getTotalElements(), page, size);
    }

    /**
     * デバイスを新規登録
     * アクセス例：POST /devices
     *
     * @param deviceFullDTO デバイス詳細情報
     * @return 新規登録されたデバイス情報
     */
    @PostMapping
    public ApiResponse<DeviceFullDTO> insertDevice(@RequestBody DeviceFullDTO deviceFullDTO) {
        log.info("デバイス新規登録リクエスト: {}", deviceFullDTO.getDeviceId());
        return deviceService.insertDevice(deviceFullDTO);
    }

    /**
     * デバイス詳細情報を取得
     * アクセス例：GET /devices/{deviceId}
     *
     * @param deviceId デバイスID
     * @return デバイス詳細情報
     */
    @GetMapping("/{deviceId}")
    public ApiResponse<DeviceFullDTO> getDeviceDetail(@PathVariable String deviceId) {
        log.info("デバイス詳細情報を照会: {}", deviceId);
        return deviceService.detail(deviceId);
    }

    /**
     * デバイス情報を更新
     * アクセス例：PUT /devices/{deviceId}
     *
     * @param deviceId      デバイスID
     * @param deviceFullDTO デバイス詳細情報
     * @return 更新後のデバイス情報
     */
    @PutMapping("/{deviceId}")
    public ApiResponse<DeviceFullDTO> updateDevice(
            @PathVariable String deviceId,
            @RequestBody DeviceFullDTO deviceFullDTO) {
        log.info("デバイス更新リクエスト: {}", deviceId);
        return deviceService.updateDeviceById(deviceId, deviceFullDTO);
    }

    /**
     * デバイスを削除
     * アクセス例：DELETE /devices/{deviceId}
     *
     * @param deviceId デバイスID
     * @return 削除成功メッセージ
     */
    @DeleteMapping("/{deviceId}")
    public ApiResponse<String> deleteDevice(@PathVariable String deviceId) {
        log.info("デバイス削除リクエスト: {}", deviceId);
        return deviceService.deleteDevice(deviceId);
    }

}
