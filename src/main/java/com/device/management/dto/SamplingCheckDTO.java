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
    @NotNull(message = "主键不能为空")
    private String samplingId;
    @NotNull(message = "抽检ID不能为空")
    private String reportId;
    @NotNull
    private String userId;
    @NotNull
    private String name;
    @NotNull
    private String deviceId;
    @NotNull
    private LocalDate updateDate; //报表导出日期
    @NotNull
    private LocalDateTime updateTime;
    @NotNull
    private LocalDateTime createTime;

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
