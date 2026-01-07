package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceUsagePermissionDTO;
import com.device.management.entity.DeviceUsagePermission;
import com.device.management.service.DeviceUsagePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/permissions")
public class DeviceUsagePermissionController {

    @Autowired
    private DeviceUsagePermissionService permissionService;

    /** IDによる権限の詳細を確認する */
    @GetMapping("/{permissionId}")
    public ApiResponse<DeviceUsagePermissionDTO> getPermissionDetail(@PathVariable String permissionId) {
        DeviceUsagePermissionDTO dto = permissionService.findPermissionDetail(permissionId);
        return ApiResponse.success("検索に成功しました", dto);
    }

    /** 権限情報を更新 */
    @PutMapping("/{permissionId}")
    public ApiResponse<Void> updatePermission(
            @PathVariable String permissionId,
            @RequestParam(required = false) String smartitStatusId,        //  smartlt_status_id
            @RequestParam(required = false) String noSmartitReason,        //  no_smartlt_reason
            @RequestParam(required = false) String usbStatusId,            //  usb_status_id
            @RequestParam(required = false) String usbReason,              //  usb_reason
            @RequestParam(required = false) LocalDate usbExpireDate,       //  usb_expire_date
            @RequestParam(required = false) String antivirusStatusId,      //  antivirus_status_id
            @RequestParam(required = false) String noSymantecReason,       //  no_symantec_reason
            @RequestParam(required = false) String domainStatusId,         //  domain_status_id
            @RequestParam(required = false) String domainGroup,            //  domain_group
            @RequestParam(required = false) String noDomainReason,         //  no_domain_reason
            @RequestParam(required = false) String remark,                 //  remark
            @RequestParam String updater) {                                //  updater

        permissionService.updatePermissionByFields(permissionId,
                smartitStatusId, noSmartitReason,
                usbStatusId, usbReason, usbExpireDate,
                antivirusStatusId, noSymantecReason,
                domainStatusId, domainGroup, noDomainReason,
                remark, updater);

        return ApiResponse.success("更新に成功しました");
    }
}