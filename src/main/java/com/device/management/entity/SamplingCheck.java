package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * サンプリングチェックエンティティ（sampling_checkテーブル）
 * サンプリングチェック詳細を保存
 */
@Data
@Entity
@Table(name = "sampling_check", schema = "public")
public class SamplingCheck {

    @Id
    @Column(name = "sampling_id", length = 50)
    private String samplingId;// サンプリングチェック番号（プライマリキー）

    @Column(name = "installed_software")
    private Boolean installedSoftware;// インストールソフトウェア

    @Column(name = "disposal_measures", columnDefinition = "TEXT")
    private String disposalMeasures;// 処置措置

    @Column(name = "name", length = 100, nullable = false)
    private String name;// 氏名

    @Column(name = "report_id", length = 50, nullable = false)
    private String reportId;// レポート番号

    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;// 更新日

    @Column(name = "screen_saver_pwd")
    private Boolean screenSaverPwd; // スクリーンセーバーパスワード状態

    @Column(name = "usb_interface")
    private Boolean usbInterface;// USBインターフェース状態

    @Column(name = "security_patch")
    private Boolean securityPatch;// セキュリティパッチ状態

    @Column(name = "antivirus_protection")
    private Boolean antivirusProtection;// ウイルス防護状態ID

    @Column(name = "boot_authentication")
    private Boolean bootAuthentication;// 起動認証状態

    @Column(name = "create_time")
    private LocalDateTime createTime; // 作成日時

    @Column(name = "creater", length = 100)
    private String creater;// 作成者

    @Column(name = "update_time")
    private LocalDateTime updateTime;// 更新日時

    @Column(name = "updater", length = 100)
    private String updater;// 更新者

    // ============= 関連関係 =============

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "device_id")
    private Device device;// 機器エンティティ（外部キー）

    @Column(name = "device_id", length = 50, insertable = false, updatable = false)
    private String deviceId;// 機器番号（外部キー）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;// ユーザエンティティ（外部キー）

    @Column(name = "user_id", length = 50)
    private String userId;// ユーザID（外部キー）

    // ============= 构造函数 =============

    public SamplingCheck() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}