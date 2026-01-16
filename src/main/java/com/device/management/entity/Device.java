package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 機器オブジェクト
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "device_info") // table name
public class Device {
    @Id
    @Column(name = "device_id", unique = true, nullable = false)
    private String deviceId; //機器番号

    @Column(name = "device_model")
    private String deviceModel; //ホストモデル

    @Column(name = "computer_name")
    private String computerName; //コンピュータ名

    @Column(name = "login_username")
    private String loginUsername; //ログインユーザ名

    @Column(name = "project")
    private String project; //所属プロジェクト

    @Column(name = "dev_room")
    private String devRoom; //所属開発室

    @Column(name = "user_id")
    private String userId; //従業員番号

    @Column(name = "remark", columnDefinition = "text")
    private String remark; //備考

    @Column(name = "self_confirm_id", columnDefinition = "bigint")
    private Integer selfConfirmId; //本人確認ID

    @Column(name = "os_id", columnDefinition = "bigint")
    private Long osId; //OSID

    @Column(name = "memory_id", columnDefinition = "bigint")
    private Long memoryId; //メモリID

    @Column(name = "ssd_id", columnDefinition = "bigint")
    private Long ssdId; //SSDID

    @Column(name = "hdd_id", columnDefinition = "bigint")
    private Long hddId; //HDDID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "self_confirm_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict selfConfirmDict; // 本人確認状態辞書

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict osDict; // os 辞書

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict memoryDict; //memory 辞書

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ssd_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict ssdDict; // SSD 辞書

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hdd_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict hddDict; // HDD 辞書

    @Column(name = "create_time", columnDefinition = "timestamp")
    private LocalDateTime createTime; //作成日時

    @Column(name = "creater")
    private String creater; //作成者

    @Column(name = "update_time", columnDefinition = "timestamp")
    private LocalDateTime updateTime; //更新日時

    @Column(name = "updater")
    private String updater; //更新者

    // ============= 関連関係 =============

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeviceIp> deviceIps;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Monitor> monitorInfos;

    @OneToOne(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DevicePermission devicePermission;

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private Set<DeviceIp> ipList = new HashSet<>();

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private Set<Monitor> monitorList = new HashSet<>();


}
