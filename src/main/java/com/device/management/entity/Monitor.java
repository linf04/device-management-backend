package com.device.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * monitorオブジェクト
 */
@Data
@Entity
@Table(name = "monitor_info")
@JsonIgnoreProperties({"device"})
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monitor_id")
    private Integer monitorId; // モニター番号

    @Column(name = "monitor_name", nullable = false, length = 100)
    private String monitorName; // 'モニター名

    @Column(name = "device_id", length = 50, nullable = false)
    private String deviceId; //機器番号

    // device:monitor（1:n）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "device_id", insertable = false, updatable = false)
    private Device device;//機器番号（外部キー）

    @Column(name = "create_time", columnDefinition = "timestamp")
    private LocalDateTime createTime; //作成日時

    @Column(name = "creater")
    private String creater; //作成者

    @Column(name = "update_time", columnDefinition = "timestamp")
    private LocalDateTime updateTime; //更新日時

    @Column(name = "updater")
    private String updater; //更新者
}