package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_permission", schema = "public")
public class DevicePermission {

    @Id
    @Column(name = "permission_id", length = 50)
    private String permissionId;

    @Column(name = "domain_group", length = 100)
    private String domainGroup;

    @Column(name = "no_domain_reason", columnDefinition = "TEXT")
    private String noDomainReason;

    @Column(name = "no_smartit_reason", columnDefinition = "TEXT")
    private String noSmartitReason;

    @Column(name = "usb_reason", columnDefinition = "TEXT")
    private String usbReason;

    @Column(name = "usb_expire_date")
    private LocalDate usbExpireDate;

    @Column(name = "no_symantec_reason", columnDefinition = "TEXT")
    private String noSymantecReason;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "creater", length = 100)
    private String creater;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "updater", length = 100)
    private String updater;

    // ============= 关联关系 =============

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "device_id")
    private Device device;

    @Column(name = "device_id", length = 50, insertable = false, updatable = false)
    private String deviceId;

    // ============= 字典关联 =============

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_status_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict domainStatus;

    @Column(name = "domain_status_id")
    private Long domainStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smartit_status_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict smartitStatus;

    @Column(name = "smartit_status_id")
    private Long smartitStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usb_status_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict usbStatus;

    @Column(name = "usb_status_id")
    private Long usbStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "antivirus_status_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict antivirusStatus;

    @Column(name = "antivirus_status_id")
    private Long antivirusStatusId;

    // ============= 构造函数 =============

    public DevicePermission() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}