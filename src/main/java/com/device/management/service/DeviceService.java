package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceFullDTO;
import org.springframework.data.domain.Page;

/**
 * デバイス管理サービスインターフェース
 * デバイスのCRUD操作、リスト照会およびExcelエクスポート機能を提供
 *
 * @author device-management
 */
public interface DeviceService {

    /**
     * デバイス追加
     * デバイスID、モニター名、IPアドレスのユニーク性チェックと形式検証を実施
     *
     * @param deviceFullDTO デバイス詳細情報、deviceIdとcreaterは必須
     * @return 追加成功したデバイス情報
     */
    ApiResponse<DeviceFullDTO> insertDevice(DeviceFullDTO deviceFullDTO);

    /**
     * デバイス更新
     * デバイス存在チェック、モニター名とIPアドレスのユニーク性チェックを実施
     *
     * @param deviceId      デバイスID、空不可
     * @param deviceFullDTO デバイス詳細情報、空不可
     * @return 更新後のデバイス情報
     */
    ApiResponse<DeviceFullDTO> updateDeviceById(String deviceId, DeviceFullDTO deviceFullDTO);

    /**
     * デバイス削除
     * 関連するモニター、IPアドレス、サンプリングチェック記録をカスケード削除
     *
     * @param deviceId デバイスID、空不可
     * @return 削除成功メッセージ
     */
    ApiResponse<String> deleteDevice(String deviceId);

    /**
     * デバイスリスト取得（ページングとフィルター）
     * デバイス名とユーザーIDによるフィルターに対応、結果はデバイスID昇順で並び替え
     *
     * @param userId     ユーザーID（任意）、完全一致用
     * @param page       ページ番号、1から開始
     * @param size       1ページ当たりのサイズ、範囲1-100
     * @return ページングされたデバイスリスト
     * @throws IllegalArgumentException   ページまたはページサイズパラメータが無効
     */
    Page<DeviceFullDTO> list(String userId, int page, int size);

    /**
     * デバイス詳細取得
     * デバイス基本情報、関連するモニターリストおよびIPアドレスリストを含む
     *
     * @param deviceId デバイスID、空不可
     * @return デバイス詳細情報
     */
    ApiResponse<DeviceFullDTO> detail(String deviceId);

}
