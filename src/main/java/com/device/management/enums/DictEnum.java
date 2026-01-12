package com.device.management.enums;

import lombok.Getter;

/**
 * 辞書列挙
 * すべての辞書タイプと対応する辞書項目IDを含む
 */
@Getter
public enum DictEnum {
    // ------------------------------
    // USER_TYPE（ユーザータイプ）
    // ------------------------------
    USER_TYPE_ADMIN(11L, "USER_TYPE", "admin"),
    USER_TYPE_USER(12L, "USER_TYPE", "user"),

    // ------------------------------
    // OS_TYPE（オペレーティングシステム種別）
    // ------------------------------
    OS_TYPE_WINDOWS_10(21L, "OS_TYPE", "Windows 10"),
    OS_TYPE_WINDOWS_11(22L, "OS_TYPE", "Windows 11"),
    OS_TYPE_MACOS_VENTURA(23L, "OS_TYPE", "macOS Ventura"),
    OS_TYPE_LINUX_UBUNTU_22_04(24L, "OS_TYPE", "Linux Ubuntu 22.04"),

    // ------------------------------
    // MEMORY_SIZE（メモリ容量）
    // ------------------------------
    MEMORY_SIZE_8GB(31L, "MEMORY_SIZE", "8GB"),
    MEMORY_SIZE_16GB(32L, "MEMORY_SIZE", "16GB"),
    MEMORY_SIZE_32GB(33L, "MEMORY_SIZE", "32GB"),
    MEMORY_SIZE_64GB(34L, "MEMORY_SIZE", "64GB"),

    // ------------------------------
    // SSD_SIZE（SSD容量）
    // ------------------------------
    SSD_SIZE_256GB(41L, "SSD_SIZE", "256GB"),
    SSD_SIZE_512GB(42L, "SSD_SIZE", "512GB"),
    SSD_SIZE_1TB(43L, "SSD_SIZE", "1TB"),
    SSD_SIZE_2TB(44L, "SSD_SIZE", "2TB"),

    // ------------------------------
    // HDD_SIZE（HDD容量）
    // ------------------------------
    HDD_SIZE_1TB(51L, "HDD_SIZE", "1TB"),
    HDD_SIZE_2TB(52L, "HDD_SIZE", "2TB"),
    HDD_SIZE_4TB(53L, "HDD_SIZE", "4TB"),

    // ------------------------------
    // CONFIRM_STATUS（本人確認ステータス）
    // ------------------------------
    CONFIRM_STATUS_CONFIRMED(61L, "CONFIRM_STATUS", "確認済み"),
    CONFIRM_STATUS_UNCONFIRMED(62L, "CONFIRM_STATUS", "未確認"),

    // ------------------------------
    // DOMAIN_STATUS（ドメインステータス）
    // ------------------------------
    DOMAIN_STATUS_JOINED(71L, "DOMAIN_STATUS", "参加済み"),
    DOMAIN_STATUS_NOT_JOINED(72L, "DOMAIN_STATUS", "未参加"),

    // ------------------------------
    // SMARTIT_STATUS（SmartITステータス）
    // ------------------------------
    SMARTIT_STATUS_INSTALLED(81L, "SMARTIT_STATUS", "インストール済み"),
    SMARTIT_STATUS_NOT_INSTALLED(82L, "SMARTIT_STATUS", "未インストール"),

    // ------------------------------
    // USB_STATUS（USBステータス）
    // ------------------------------
    USB_STATUS_ALLOWED(91L, "USB_STATUS", "許可"),
    USB_STATUS_FORBIDDEN(92L, "USB_STATUS", "禁止"),
    USB_STATUS_TEMPORARY_ALLOWED(93L, "USB_STATUS", "一時許可"),

    // ------------------------------
    // ANTIVIRUS_STATUS（アンチウイルスソフトウェアステータス）
    // ------------------------------
    ANTIVIRUS_STATUS_INSTALLED(101L, "ANTIVIRUS_STATUS", "インストール済み"),
    ANTIVIRUS_STATUS_NOT_INSTALLED(102L, "ANTIVIRUS_STATUS", "未インストール"),
    ANTIVIRUS_STATUS_EXPIRED(103L, "ANTIVIRUS_STATUS", "有効期限切れ");

    private final Long dictId;
    private final String typeCode;
    private final String itemName;

    DictEnum(Long dictId, String typeCode, String itemName) {
        this.dictId = dictId;
        this.typeCode = typeCode;
        this.itemName = itemName;
    }

    /**
     * 辞書IDで辞書列挙を検索
     */
    public static DictEnum findByDictId(Long dictId) {
        for (DictEnum dictEnum : values()) {
            if (dictEnum.getDictId().equals(dictId)) {
                return dictEnum;
            }
        }
        return null;
    }

    /**
     * 辞書タイプコードで辞書列挙を検索
     */
    public static DictEnum[] findByTypeCode(String typeCode) {
        return java.util.Arrays.stream(values())
                .filter(dictEnum -> dictEnum.getTypeCode().equals(typeCode))
                .toArray(DictEnum[]::new);
    }
}
