package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "device_permission")
public class DeviceUsagePermission {
    
    @Id
    @Column(name = "permission_id", length = 50)
    private String permissionId;

    @Column(name = "domain_status_id")
    private Long domainStatusId;
    
    @Column(name = "domain_group", length = 100)
    private String domainGroup;
    
    @Column(name = "no_domain_reason", columnDefinition = "TEXT")
    private String noDomainReason;
    
    @Column(name = "smartit_status_id")
    private Long smartitStatusId;
    
    @Column(name = "no_smartit_reason", columnDefinition = "TEXT")
    private String noSmartitReason;
    
    @Column(name = "usb_status_id")
    private Long usbStatusId;
    
    @Column(name = "usb_reason", columnDefinition = "TEXT")
    private String usbReason;
    
    @Column(name = "usb_expire_date")
    @Temporal(TemporalType.DATE)
    private Date usbExpireDate;
    
    @Column(name = "antivirus_status_id")
    private Long antivirusStatusId;
    
    @Column(name = "no_symantec_reason", columnDefinition = "TEXT")
    private String noSymantecReason;
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    
    @Column(name = "update_time")
    private LocalDateTime updateTime = LocalDateTime.now();
    
    @Column(name = "updater", length = 100)
    private String updater;
}