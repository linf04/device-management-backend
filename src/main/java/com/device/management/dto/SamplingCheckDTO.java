package com.device.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SamplingCheckDTO {
    private String samplingId;  //主キー、データベースの非空フィールド、バックエンドで管理

    @NotNull(message = "レポートIDは空にできません")
    private String reportId;
    @NotNull(message = "ユーザーIDは空にできません" )
    private String userId;
    @NotNull(message = "ユーザー名は空にできません")
    private String name;
    @NotNull(message = "機器番号は空にできません")
    private String deviceId;
    @NotNull(message = "レポートのエクスポート時間は空にできません")
    private LocalDate updateDate;

    private LocalDateTime updateTime;  // 更新時間、データベースの非空フィールド、バックエンドで管理
    private LocalDateTime createTime;  // 作成時間、データベースの非空フィールド、バックエンドで管理
    private String updater;
    private String creater;
    private Boolean installedSoftware;
    private String disposalMeasures;
    private Boolean screenSaverPwd;
    private Boolean usbInterface;
    private Boolean securityPatch;
    private Boolean antivirusProtection;
    private Boolean bootAuthentication;
}
