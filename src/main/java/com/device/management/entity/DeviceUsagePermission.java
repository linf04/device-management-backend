package com.device.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_permission", schema = "public")
public class DeviceUsagePermission {

    @Id
    @Column(name = "permission_id", length = 50, nullable = false)
    private String permissionId;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "device_id",
            foreignKey = @ForeignKey(name = "fk_permission_device"))
    private DeviceInfo device;

    @ManyToOne
    @JoinColumn(name = "domain_status_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_permission_domain"))
    private Dict domainStatus;

    @Column(name = "domain_group", length = 100)
    private String domainGroup;

    @Column(name = "no_domain_reason", columnDefinition = "text")
    private String noDomainReason;

    @ManyToOne
    @JoinColumn(name = "smartit_status_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_permission_smartit"))
    private Dict smartitStatus;

    @Column(name = "no_smartit_reason", columnDefinition = "text")
    private String noSmartitReason;

    @ManyToOne
    @JoinColumn(name = "usb_status_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_permission_usb"))
    private Dict usbStatus;

    @Column(name = "usb_reason", columnDefinition = "text")
    private String usbReason;

    @Column(name = "usb_expire_date")
    private LocalDate usbExpireDate;

    @ManyToOne
    @JoinColumn(name = "antivirus_status_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_permission_antivirus"))
    private Dict antivirusStatus;

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