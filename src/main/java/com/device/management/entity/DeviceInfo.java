package com.device.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "device_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfo {
    @Id
    @Size(max = 50)
    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Size(max = 100)
    @Column(name = "device_model", length = 100)
    private String deviceModel;

    @Size(max = 100)
    @Column(name = "computer_name", length = 100)
    private String computerName;

    @Size(max = 100)
    @Column(name = "login_username", length = 100)
    private String loginUsername;

    @Size(max = 100)
    @Column(name = "project", length = 100)
    private String project;

    @Size(max = 100)
    @Column(name = "dev_room", length = 100)
    private String devRoom;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "remark", length = Integer.MAX_VALUE)
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "self_confirm_id")
    private Dict selfConfirm;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "os_id")
    private Dict os;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "memory_id")
    private Dict memory;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "ssd_id")
    private Dict ssd;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "hdd_id")
    private Dict hdd;

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