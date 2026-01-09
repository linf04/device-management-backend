package com.device.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "device_permission")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevicePermission {
    @Id
    @Size(max = 50)
    @Column(name = "permission_id", nullable = false, length = 50)
    private String permissionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceInfo device;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "domain_status_id")
    private Dict domainStatus;

    @Size(max = 100)
    @Column(name = "domain_group", length = 100)
    private String domainGroup;

    @Column(name = "no_domain_reason", length = Integer.MAX_VALUE)
    private String noDomainReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "smartit_status_id")
    private Dict smartitStatus;

    @Column(name = "no_smartit_reason", length = Integer.MAX_VALUE)
    private String noSmartitReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "usb_status_id")
    private Dict usbStatus;

    @Column(name = "usb_reason", length = Integer.MAX_VALUE)
    private String usbReason;

    @Column(name = "usb_expire_date")
    private LocalDate usbExpireDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "antivirus_status_id")
    private Dict antivirusStatus;

    @Column(name = "no_symantec_reason", length = Integer.MAX_VALUE)
    private String noSymantecReason;

    @Column(name = "remark", length = Integer.MAX_VALUE)
    private String remark;

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