package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * デバイス権限エンティティ（device_permissionテーブル）
 * 機器のドメイン、USB等の権限を保存：機器単位管理
 */
@Data
@Entity
@Table(name = "device_permission", schema = "public")
public class DevicePermission {

    @Id
    @Column(name = "permission_id", length = 50)
    private String permissionId;// 権限番号（プライマリキー）

    @Column(name = "domain_group", length = 100)
    private String domainGroup;// ドメイン内グループ名

    @Column(name = "no_domain_reason", columnDefinition = "TEXT")
    private String noDomainReason;// ドメイン未参加理由

    @Column(name = "no_smartit_reason", columnDefinition = "TEXT")
    private String noSmartitReason;// SmartIT未インストール理由

    @Column(name = "usb_reason", columnDefinition = "TEXT")
    private String usbReason;// USB使用許可理由

    @Column(name = "usb_expire_date")
    private LocalDate usbExpireDate;// USB使用有効期限

    @Column(name = "no_symantec_reason", columnDefinition = "TEXT")
    private String noSymantecReason;// Symantec未導入理由

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;// 備考

    @Column(name = "create_time")
    private LocalDateTime createTime;// 作成日時

    @Column(name = "creater", length = 100)
    private String creater;// 作成者

    @Column(name = "update_time")
    private LocalDateTime updateTime;// 更新日時

    @Column(name = "updater", length = 100)
    private String updater;// 更新者

    // ============= 関連関係 =============

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "device_id")
    private Device device; // 機器エンティティ（外部キー：device_info関連）

    @Column(name = "device_id", length = 50, insertable = false, updatable = false)
    private String deviceId;// 機器番号（外部キー：device_info関連）

    // ============= 辞書の関連 =============

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_status_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict domainStatus;// ドメイン状態辞書（辞書項目：DOMAIN_STATUS関連）

    @Column(name = "domain_status_id")
    private Long domainStatusId;// ドメイン状態ID（辞書項目：DOMAIN_STATUS関連）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smartit_status_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict smartitStatus;// SmartIT状態辞書（辞書項目：SMARTIT_STATUS関連）

    @Column(name = "smartit_status_id")
    private Long smartitStatusId;// SmartIT状態ID（辞書項目：SMARTIT_STATUS関連）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usb_status_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict usbStatus;// USB状態辞書（辞書項目：USB_STATUS関連）

    @Column(name = "usb_status_id")
    private Long usbStatusId;// USB状態ID（辞書項目：USB_STATUS関連）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "antivirus_status_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict antivirusStatus;// アンチウイルス状態辞書（辞書項目：ANTIVIRUS_STATUS関連）

    @Column(name = "antivirus_status_id")
    private Long antivirusStatusId;// アンチウイルス状態ID（辞書項目：ANTIVIRUS_STATUS関連）

    // ============= 构造函数 =============

    public DevicePermission() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}