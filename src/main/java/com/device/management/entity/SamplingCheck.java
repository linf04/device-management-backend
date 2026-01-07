package com.device.management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sampling_check", schema = "public")
public class SamplingCheck {
    @Id
    private String samplingId;

    private String name;
    private String  userId;
    private String reportId;
    private String  deviceId;
    private LocalDate updateDate; //报表导出日期
    private Boolean installedSoftware;
    private String disposalMeasures;
    private Boolean screenSaverPwd;
    private Boolean usbInterface;
    private Boolean securityPatch;
    private Boolean antivirusProtection;
    private Boolean bootAuthentication;
    private LocalDateTime createTime;
    private String creater;
    private LocalDateTime updateTime;
    private String updater;
}