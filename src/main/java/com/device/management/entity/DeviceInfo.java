package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_info", schema = "public")
public class DeviceInfo {

    @Id
    @Column(name = "device_id", length = 50, nullable = false)
    private String deviceId;

    @Column(name = "device_model", length = 100)
    private String deviceModel;

    @Column(name = "computer_name", length = 100)
    private String computerName;

    @Column(name = "login_username", length = 100)
    private String loginUsername;

    @Column(name = "project", length = 100)
    private String project;

    @Column(name = "dev_room", length = 100)
    private String devRoom;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name = "fk_device_user"))
    private User user;

    @Column(name = "remark", columnDefinition = "text")
    private String remark;

    @ManyToOne
    @JoinColumn(name = "self_confirm_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_device_self_confirm"))
    private Dict selfConfirm;

    @ManyToOne
    @JoinColumn(name = "os_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_device_os"))
    private Dict os;

    @ManyToOne
    @JoinColumn(name = "memory_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_device_memory"))
    private Dict memory;

    @ManyToOne
    @JoinColumn(name = "ssd_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_device_ssd"))
    private Dict ssd;

    @ManyToOne
    @JoinColumn(name = "hdd_id", referencedColumnName = "dict_id",
            foreignKey = @ForeignKey(name = "fk_device_hdd"))
    private Dict hdd;

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