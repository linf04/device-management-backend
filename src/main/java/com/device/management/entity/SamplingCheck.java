package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sampling_check", schema = "public")
public class SamplingCheck {
    @Id
    @Column(name = "sampling_id", nullable = false, length = 50)
    private String samplingId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "report_id", nullable = false, length = 50)
    private String reportId;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate; // 报表导出日期

    @Column(name = "installed_software")
    private Boolean installedSoftware;

    @Column(name = "disposal_measures")
    private String disposalMeasures;

    @Column(name = "screen_saver_pwd")
    private Boolean screenSaverPwd;

    @Column(name = "usb_interface")
    private Boolean usbInterface;

    @Column(name = "security_patch")
    private Boolean securityPatch;

    @Column(name = "antivirus_protection")
    private Boolean antivirusProtection;

    @Column(name = "boot_authentication")
    private Boolean bootAuthentication;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "creater", length = 100)
    private String creater;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "updater", length = 100)
    private String updater;
}