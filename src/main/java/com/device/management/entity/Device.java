package com.device.management.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
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

    @Column(name = "create_time", columnDefinition = "timestamp")
    private LocalDateTime createTime; //作成日時

    @Column(name = "creater")
    private String creater; //作成者

    @Column(name = "update_time", columnDefinition = "timestamp")
    private LocalDateTime updateTime; //更新日時

    @Column(name = "updater")
    private String updater; //更新者
}
