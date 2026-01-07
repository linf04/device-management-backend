package com.device.management.service;

import com.device.management.dto.DeviceUsagePermissionDTO;
import com.device.management.entity.DeviceUsagePermission;
import com.device.management.exception.ResourceNotFoundException;
import com.device.management.repository.DeviceUsagePermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class DeviceUsagePermissionService {

    @Autowired
    private DeviceUsagePermissionRepository permissionRepository;

    /**
     * IDに基づいて権限の詳細を照会する
     */
    @Transactional(readOnly = true)
    public DeviceUsagePermissionDTO findPermissionDetail(String permissionId) {
        log.info("権限の詳細を確認する，permissionId: {}", permissionId);

        DeviceUsagePermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> {
                    log.error("権限情報が存在しません，permissionId: {}", permissionId);
                    return new ResourceNotFoundException("権限情報が存在しません，permissionId: " + permissionId);
                });

        // DTO
        return convertToDTO(permission);
    }

    /**
     * 権限情報を更新
     */
    @Transactional
    public void updatePermission(String permissionId, DeviceUsagePermission updatedData) {
        log.info("権限情報を更新，permissionId: {}", permissionId);

        DeviceUsagePermission existing = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("権限情報が存在しません"));

        // 更新
        if (updatedData.getDomainStatus() != null) {
            existing.setDomainStatus(updatedData.getDomainStatus());
        }
        if (updatedData.getDomainGroup() != null) {
            existing.setDomainGroup(updatedData.getDomainGroup());
        }
        if (updatedData.getNoDomainReason() != null) {
            existing.setNoDomainReason(updatedData.getNoDomainReason());
        }
        if (updatedData.getSmartitStatus() != null) {
            existing.setSmartitStatus(updatedData.getSmartitStatus());
        }
        if (updatedData.getNoSmartitReason() != null) {
            existing.setNoSmartitReason(updatedData.getNoSmartitReason());
        }
        if (updatedData.getUsbStatus() != null) {
            existing.setUsbStatus(updatedData.getUsbStatus());
        }
        if (updatedData.getUsbReason() != null) {
            existing.setUsbReason(updatedData.getUsbReason());
        }
        if (updatedData.getUsbExpireDate() != null) {
            existing.setUsbExpireDate(updatedData.getUsbExpireDate());
        }
        if (updatedData.getAntivirusStatus() != null) {
            existing.setAntivirusStatus(updatedData.getAntivirusStatus());
        }
        if (updatedData.getNoSymantecReason() != null) {
            existing.setNoSymantecReason(updatedData.getNoSymantecReason());
        }
        if (updatedData.getRemark() != null) {
            existing.setRemark(updatedData.getRemark());
        }
        if (updatedData.getUpdater() != null) {
            existing.setUpdater(updatedData.getUpdater());
        }

        permissionRepository.save(existing);
    }

    /** フィールドに基づいて権限情報を更新する */
    @Transactional
    public void updatePermissionByFields(String permissionId,
                                         String smartitStatusId,
                                         String noSmartitReason,
                                         String usbStatusId,
                                         String usbReason,
                                         LocalDate usbExpireDate,
                                         String antivirusStatusId,
                                         String noSymantecReason,
                                         String domainStatusId,
                                         String domainGroup,
                                         String noDomainReason,
                                         String remark,
                                         String updater) {

        log.info("フィールドに基づいて権限情報を更新する，ID: {}", permissionId);

        DeviceUsagePermission existing = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("権限情報が存在しません"));


        if (StringUtils.hasText(noSmartitReason)) {
            existing.setNoSmartitReason(noSmartitReason);
        }
        if (StringUtils.hasText(usbReason)) {
            existing.setUsbReason(usbReason);
        }
        if (StringUtils.hasText(noSymantecReason)) {
            existing.setNoSymantecReason(noSymantecReason);
        }
        if (StringUtils.hasText(domainGroup)) {
            existing.setDomainGroup(domainGroup);
        }
        if (StringUtils.hasText(noDomainReason)) {
            existing.setNoDomainReason(noDomainReason);
        }
        if (StringUtils.hasText(remark)) {
            existing.setRemark(remark);
        }
        if (StringUtils.hasText(updater)) {
            existing.setUpdater(updater);
        }

        if (usbExpireDate != null) {
            existing.setUsbExpireDate(usbExpireDate);
        }

        permissionRepository.save(existing);
        log.info("権限情報の更新が完了しました，permissionId: {}", permissionId);
    }

    /**
     * DTO
     */
    private DeviceUsagePermissionDTO convertToDTO(DeviceUsagePermission permission) {
        DeviceUsagePermissionDTO dto = new DeviceUsagePermissionDTO();

        // 権限基本情報
        dto.setPermissionId(permission.getPermissionId());

        // デバイス情報
        if (permission.getDevice() != null) {
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
            dto.setDomainStatus(permission.getDomainStatus().getDictItemName());
        }
        dto.setDomainGroup(permission.getDomainGroup());
        dto.setNoDomainReason(permission.getNoDomainReason());

        // SmartITステータス情報
        if (permission.getSmartitStatus() != null) {
            dto.setSmartitStatus(permission.getSmartitStatus().getDictItemName());
        }
        dto.setNoSmartitReason(permission.getNoSmartitReason());

        // USBステータス情報
        if (permission.getUsbStatus() != null) {
            dto.setUsbStatus(permission.getUsbStatus().getDictItemName());
        }
        dto.setUsbReason(permission.getUsbReason());
        dto.setUsbExpireDate(permission.getUsbExpireDate());

        // アンチウイルスソフトのステータス情報
        if (permission.getAntivirusStatus() != null) {
            dto.setAntivirusStatus(permission.getAntivirusStatus().getDictItemName());
        }
        dto.setNoSymantecReason(permission.getNoSymantecReason());

        dto.setRemark(permission.getRemark());
        dto.setCreateTime(permission.getCreateTime());
        dto.setCreater(permission.getCreater());
        dto.setUpdateTime(permission.getUpdateTime());
        dto.setUpdater(permission.getUpdater());

        return dto;
    }
}