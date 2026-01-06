package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sampling_check", schema = "public")
public class SamplingCheck {

    @Id
    @Column(name = "sampling_id", length = 50)
    private String samplingId;

    @Column(name = "installed_software")
    private Boolean installedSoftware;

    @Column(name = "disposal_measures", columnDefinition = "TEXT")
    private String disposalMeasures;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "report_id", length = 50, nullable = false)
    private String reportId;

    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;

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

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "creater", length = 100)
    private String creater;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "updater", length = 100)
    private String updater;

    // ============= 关联关系 =============

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "device_id")
    private Device device;

    @Column(name = "device_id", length = 50, insertable = false, updatable = false)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id", length = 50)
    private String userId;

    // ============= 构造函数 =============

    public SamplingCheck() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}