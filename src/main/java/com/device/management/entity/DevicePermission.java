package com.device.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "device_permission", schema = "public")
public class DevicePermission {
    
    @Id
    @Column(name = "permission_id", length = 50, nullable = false)
    private String permissionId;

    @OneToOne
    @JoinColumn(name = "device_id", referencedColumnName = "device_id",
            foreignKey = @ForeignKey(name = "fk_permission_device"))
    private Device device;

    @Column(name = "domain_status_id")
    private Long domainStatusId;

    @Column(name = "domain_group", length = 100)
    private String domainGroup;

    @Column(name = "no_domain_reason", columnDefinition = "text")
    private String noDomainReason;

    @Column(name = "smartit_status_id")
    private Long smartitStatusId;

    @Column(name = "no_smartit_reason", columnDefinition = "text")
    private String noSmartitReason;

    @Column(name = "usb_status_id")
    private Long usbStatusId;

    @Column(name = "usb_reason", columnDefinition = "text")
    private String usbReason;
    
    @Column(name = "usb_expire_date")
    private LocalDate usbExpireDate;

    @Column(name = "antivirus_status_id")
    private Long antivirusStatusId;

    @Column(name = "no_symantec_reason", columnDefinition = "text")
    private String noSymantecReason;

    @Column(name = "remark", columnDefinition = "text")
    private String remark;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "creater", length = 100)
    private String creater;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "updater", length = 100)
    private String updater;
}