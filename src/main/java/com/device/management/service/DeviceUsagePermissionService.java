package com.device.management.service;

import com.device.management.dto.DeviceUsagePermissionDTO;
import com.device.management.entity.DevicePermission;
import com.device.management.entity.Dict;
import com.device.management.exception.ConflictException;
import com.device.management.exception.ResourceNotFoundException;
import com.device.management.repository.DeviceUsagePermissionRepository;
import com.device.management.repository.DictRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class DeviceUsagePermissionService {
    @Resource
    private DeviceUsagePermissionRepository permissionRepository;
    @Autowired
    private DictRepository dictRepository;

    @Transactional
    /**
     * IDに基づいてデバイス使用権限を削除
     * @param id 権限ID（APIレイヤーのLong型）
     */
    public void deletePermissionById(String permissionId) {
        // 2. 権限の存在チェック
        DevicePermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("権限が存在しません: " + permissionId));

        // 4. TODO: 関連リソースのチェック（他のテーブルをクエリする必要あり）
        // 模擬：権限IDに"TEST"が含まれている場合、関連があるものとみなす
        if (permissionId.contains("TEST")) {
            throw new ConflictException("権限は既にリソースに紐づいているため、削除できません: " + permissionId);
        }

        // 5. 物理削除の実行
        permissionRepository.delete(permission);
    }
    /**
     * IDに基づき使用権限の詳細情報を取得します。
     *  1. 権限IDをパラメータとして受け取ります。
     *  2. リポジトリ層を介して対応する権限エンティティを検索します。
     *  3. 存在しない場合はリソース未検出例外をスローします。
     *  4. エンティティオブジェクトをDTOオブジェクトに変換します。
     *  5. DTOオブジェクトを呼び出し元に返します。
     * @param permissionId 権限ID
     * @return 権限詳細情報DTOオブジェクト
     */
    @Transactional(readOnly = true)
    public DeviceUsagePermissionDTO findPermissionDetail(String permissionId) {
        log.info("権限の詳細を確認する，permissionId: {}", permissionId);

        //IDに基づいてデータベースから権限オブジェクトを取得する
        DevicePermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> {
                    log.error("権限情報が存在しません，permissionId: {}", permissionId);
                    // 存在しない場合は例外をスローします。
                    return new ResourceNotFoundException("権限情報が存在しません，permissionId: " + permissionId);
                });

        // エンティティオブジェクトをDTOオブジェクトに変換して返却する
        return convertToDTO(permission);
    }

    /**
     * フィールド単位で権限情報を更新します。
     *  1. IDに基づき既存の権限エンティティを検索します。
     *  2. 各パラメータに値があるか確認します（StringUtilsを使用して文字列判定を行います）。
     *  3. 値があるパラメータを既存のエンティティに更新します。
     *  4. 更新後のエンティティを保存します。
     */
    @Transactional
    public void updatePermissionByFields(String permissionId, DeviceUsagePermissionDTO updateDTO) {
        log.info("フィールドに基づいて権限情報を更新する，ID: {}", permissionId);

        //既存の権限エンティティを取得します。
        DevicePermission existing = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("権限情報が存在しません"));

        //パラメータを一つずつ確認して更新する
        // SmartITステータス更新
        if (updateDTO.getSmartitStatusId()!=null) {
            Dict smartitDict = dictRepository.findById(updateDTO.getSmartitStatusId().toString())
                    .orElseThrow(() -> new ResourceNotFoundException("SmartITステータスが存在しません"));
            existing.setSmartitStatus(smartitDict);
        }

        if (StringUtils.hasText(updateDTO.getNoSmartitReason())) {
            existing.setNoSmartitReason(updateDTO.getNoSmartitReason());
        }

        // USBステータス更新
        if (updateDTO.getUsbStatusId()!=null) {
            Dict usbDict = dictRepository.findById(updateDTO.getUsbStatusId().toString())
                    .orElseThrow(() -> new ResourceNotFoundException("USBステータスが存在しません"));
            existing.setUsbStatus(usbDict);
        }

        if (StringUtils.hasText(updateDTO.getUsbReason())) {
            existing.setUsbReason(updateDTO.getUsbReason());
        }

        // アンチウイルスステータス更新
        if (updateDTO.getAntivirusStatusId()!=null) {
            Dict antivirusDict = dictRepository.findById(updateDTO.getAntivirusStatusId().toString())
                    .orElseThrow(() -> new ResourceNotFoundException("アンチウイルスステータスが存在しません"));
            existing.setAntivirusStatus(antivirusDict);
        }

        if (StringUtils.hasText(updateDTO.getNoSymantecReason())) {
            existing.setNoSymantecReason(updateDTO.getNoSymantecReason());
        }

        // ドメインステータス更新
        if (updateDTO.getDomainStatusId()!=null) {
            Dict domainDict = dictRepository.findById(updateDTO.getDomainStatusId().toString())
                    .orElseThrow(() -> new ResourceNotFoundException("ドメインステータスが存在しません"));
            existing.setDomainStatus(domainDict);
        }

        if (StringUtils.hasText(updateDTO.getDomainGroup())) {
            existing.setDomainGroup(updateDTO.getDomainGroup());
        }

        if (StringUtils.hasText(updateDTO.getNoDomainReason())) {
            existing.setNoDomainReason(updateDTO.getNoDomainReason());
        }

        if (StringUtils.hasText(updateDTO.getRemark())) {
            existing.setRemark(updateDTO.getRemark());
        }

        //更新者を設定する（現在のユーザーを取得）
        existing.setUpdater("JS111");

        if (updateDTO.getUsbExpireDate() != null) {
            existing.setUsbExpireDate(updateDTO.getUsbExpireDate());
        }

        //保存して更新
        permissionRepository.save(existing);
        log.info("権限情報の更新が完了しました，permissionId: {}", permissionId);
    }

    /**
     * DTOへの変換メソッド
     * エンティティオブジェクトをDTOオブジェクトに変換する方法
     * @param permission 権限エンティティオブジェクト
     * @return 変換後のDTOオブジェクト
     */
    private DeviceUsagePermissionDTO convertToDTO(DevicePermission permission) {
        DeviceUsagePermissionDTO dto = new DeviceUsagePermissionDTO();

        // 権限基本IDを設定
        dto.setPermissionId(permission.getPermissionId());

        // デバイス情報マッピング
        if (permission.getDevice() != null) {
            //デバイス情報
            dto.setDeviceId(permission.getDevice().getDeviceId());
            dto.setComputerName(permission.getDevice().getComputerName());
            dto.setDeviceModel(permission.getDevice().getDeviceModel());
            dto.setProject(permission.getDevice().getProject());
            dto.setDevRoom(permission.getDevice().getDevRoom());

            // ユーザー情報
            if (permission.getDevice().getUser() != null) {
                dto.setUserId(permission.getDevice().getUser().getUserId());
                dto.setUserName(permission.getDevice().getUser().getName());
                dto.setDeptId(permission.getDevice().getUser().getDeptId());
            }
        }

        // ドメインステータス情報
        if (permission.getDomainStatus() != null) {
            dto.setDomainStatusId(permission.getDomainStatus().getId());
            dto.setDomainStatusName(permission.getDomainStatus().getDictItemName());
        }
        dto.setDomainGroup(permission.getDomainGroup());
        dto.setNoDomainReason(permission.getNoDomainReason());

        // SmartITステータス情報
        if (permission.getSmartitStatus() != null) {
            dto.setSmartitStatusId(permission.getSmartitStatus().getId());
            dto.setSmartitStatusName(permission.getSmartitStatus().getDictItemName());
        }
        dto.setNoSmartitReason(permission.getNoSmartitReason());

        // USBステータス情報
        if (permission.getUsbStatus() != null) {
            dto.setUsbStatusId(permission.getUsbStatus().getId());
            dto.setUsbStatusName(permission.getUsbStatus().getDictItemName());
        }
        dto.setUsbReason(permission.getUsbReason());
        dto.setUsbExpireDate(permission.getUsbExpireDate());

        // アンチウイルスソフトのステータス情報
        if (permission.getAntivirusStatus() != null) {
            dto.setAntivirusStatusId(permission.getAntivirusStatus().getId());
            dto.setAntivirusStatusName(permission.getAntivirusStatus().getDictItemName());
        }
        dto.setNoSymantecReason(permission.getNoSymantecReason());

        //情報の作成と更新
        dto.setRemark(permission.getRemark());
        dto.setCreateTime(permission.getCreateTime());
        dto.setCreater(permission.getCreater());
        dto.setUpdateTime(permission.getUpdateTime());
        dto.setUpdater(permission.getUpdater());

        return dto;
    }
}