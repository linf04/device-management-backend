package com.device.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "device_ip")
public class DeviceIp {
    @Id
    @Column(name = "ip_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceInfo device;

    @Size(max = 50)
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

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