package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionUpdateDTO {
    private String permissionId;
    private String deviceId;
    private String computerName;
    private String deviceModel;
    private String project;
    private String devRoom;

    //ユーザー情報（device_info.user_idを通じてusersテーブルと関連付け）
    private String userId;
    private String userName;
    private String deptId;

    //権限状態（dict表を通じて関連付け）
    private Long domainStatus;
    private String domainStatusName;
    private String domainGroup;
    private String noDomainReason;

    private Long smartitStatus;
    private String smartitStatusName;
    private String noSmartitReason;

    private Long usbStatus;
    private String usbStatusName;
    private String usbReason;
    private LocalDate usbExpireDate;

    private Long antivirusStatus;
    private String antivirusStatusName;
    private String noSymantecReason;

    private String remark;

    private LocalDateTime createTime;
    private String creater;
    private LocalDateTime updateTime;
    private String updater;
}