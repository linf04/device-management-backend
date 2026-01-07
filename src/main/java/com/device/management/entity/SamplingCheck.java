package com.device.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "sampling_check")
public class SamplingCheck {
    @Id
    @Size(max = 50)
    @Column(name = "sampling_id", nullable = false, length = 50)
    private String samplingId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private User userId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceInfo device;

    @Column(name = "installed_software")
    private Boolean installedSoftware;

    @Column(name = "disposal_measures", length = Integer.MAX_VALUE)
    private String disposalMeasures;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "report_id", nullable = false, length = 50)
    private String reportId;

    @NotNull
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

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @Size(max = 100)
    @Column(name = "creater", length = 100)
    private String creater;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    @Size(max = 100)
    @Column(name = "updater", length = 100)
    private String updater;


}