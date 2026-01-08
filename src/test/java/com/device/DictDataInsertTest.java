package com.device;

import com.device.management.DeviceManagementApplication;
import com.device.management.repository.DictRepository;
import com.device.management.entity.Dict;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = DeviceManagementApplication.class)
public class DictDataInsertTest {

    @Autowired
    private DictRepository dictRepository;

    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    @Test
    public void insertAllDictData() {
        List<Dict> dictList = new ArrayList<>();

        // ------------------------------
        // USER_TYPE（用户类型）
        // ------------------------------
        dictList.add(buildDict(11L, "USER_TYPE", "ユーザータイプ", "システムユーザーロール",
                "admin", 1, (short) 1,
                "2026-01-04 22:23:01.290470", "admin",
                "2026-01-04 23:45:00.038927", "admin"));
        dictList.add(buildDict(12L, "USER_TYPE", "ユーザータイプ", "システムユーザーロール",
                "user", 2, (short) 1,
                "2026-01-04 22:23:01.290470", "admin",
                "2026-01-04 23:45:02.154890", "admin"));
        // ------------------------------
        // OS_TYPE（操作系统类型）
        // ------------------------------
        dictList.add(buildDict(21L, "OS_TYPE", "オペレーティングシステム種別", "デバイスで使用されるOSの種別",
                "Windows 10", 1, (short) 1,
                "2026-01-04 23:43:32.499654", "admin",
                "2026-01-04 23:45:07.113395", "admin"));
        dictList.add(buildDict(22L, "OS_TYPE", "オペレーティングシステム種別", "デバイスで使用されるOSの種別",
                "Windows 11", 2, (short) 1,
                "2026-01-04 23:43:32.499654", "admin",
                "2026-01-04 23:45:08.975521", "admin"));
        dictList.add(buildDict(23L, "OS_TYPE", "オペレーティングシステム種別", "デバイスで使用されるOSの種別",
                "macOS Ventura", 3, (short) 1,
                "2026-01-04 23:43:32.499654", "admin",
                "2026-01-04 23:45:11.710822", "admin"));
        dictList.add(buildDict(24L, "OS_TYPE", "オペレーティングシステム種別", "デバイスで使用されるOSの種別",
                "Linux Ubuntu 22.04", 4, (short) 1,
                "2026-01-04 23:43:32.499654", "admin",
                "2026-01-04 23:45:13.661055", "admin"));

        // ------------------------------
        // MEMORY_SIZE（内存容量）
        // ------------------------------
        dictList.add(buildDict(34L, "MEMORY_SIZE", "メモリ容量", "デバイスの物理メモリ容量",
                "64GB",4,(short) 1,
                "2026-01-04 23:43:32.500961", "admin",
                "2026-01-04 23:46:51.044415", "admin"));
        dictList.add(buildDict(33L, "MEMORY_SIZE", "メモリ容量", "デバイスの物理メモリ容量",
                "32GB",3,(short) 1,
                "2026-01-04 23:43:32.500961", "admin",
                "2026-01-04 23:46:52.712702", "admin"));
        dictList.add(buildDict(32L, "MEMORY_SIZE", "メモリ容量", "デバイスの物理メモリ容量",
                "16GB",2,(short) 1,
                "2026-01-04 23:43:32.500961", "admin",
                "2026-01-04 23:46:53.865544", "admin"));
        dictList.add(buildDict(31L, "MEMORY_SIZE", "メモリ容量", "デバイスの物理メモリ容量",
                "8GB",1,(short) 1,
                "2026-01-04 23:43:32.500961", "admin",
                "2026-01-04 23:47:01.488466", "admin"));

        // ------------------------------
        // SSD_SIZE（SSD容量）
        // ------------------------------
        dictList.add(buildDict(44L, "SSD_SIZE", "SSD容量", "デバイスのSSDストレージ容量",
                "2TB",4,(short) 1,
                "2026-01-04 23:43:32.501325", "admin",
                "2026-01-04 23:46:42.179930", "admin"));
        dictList.add(buildDict(43L, "SSD_SIZE", "SSD容量", "デバイスのSSDストレージ容量",
                "1TB",3,(short) 1,
                "2026-01-04 23:43:32.501325", "admin",
                "2026-01-04 23:46:44.038207", "admin"));
        dictList.add(buildDict(42L, "SSD_SIZE", "SSD容量", "デバイスのSSDストレージ容量",
                "512GB",2,(short) 1,
                "2026-01-04 23:43:32.501325", "admin",
                "2026-01-04 23:46:45.355822", "admin"));
        dictList.add(buildDict(41L, "SSD_SIZE", "SSD容量", "デバイスのSSDストレージ容量",
                "256GB",1,(short) 1,
                "2026-01-04 23:43:32.501325", "admin",
                "2026-01-04 23:46:48.431263", "admin"));

        // ------------------------------
        // HDD_SIZE（HDD容量）
        // ------------------------------
        dictList.add(buildDict(53L, "HDD_SIZE", "HDD容量", "デバイスのHDDストレージ容量",
                "4TB",3,(short) 1,
                "2026-01-04 23:43:32.501787", "admin",
                "2026-01-04 23:46:37.246191", "admin"));
        dictList.add(buildDict(52L, "HDD_SIZE", "HDD容量", "デバイスのHDDストレージ容量",
                "2TB",2,(short) 1,
                "2026-01-04 23:43:32.501787", "admin",
                "2026-01-04 23:46:38.270224", "admin"));
        dictList.add(buildDict(51L, "HDD_SIZE", "HDD容量", "デバイスのHDDストレージ容量",
                "1TB",1,(short) 1,
                "2026-01-04 23:43:32.501787", "admin",
                "2026-01-04 23:46:40.224721", "admin"));

        // ------------------------------
        // CONFIRM_STATUS（本人确认状态）
        // ------------------------------
        dictList.add(buildDict(62L, "CONFIRM_STATUS", "本人確認ステータス", "デバイスの本人確認ステータス",
                "未確認",2,(short) 1,
                "2026-01-04 23:43:32.502066", "admin",
                "2026-01-04 23:46:32.930814", "admin"));
        dictList.add(buildDict(61L, "CONFIRM_STATUS", "本人確認ステータス", "デバイスの本人確認ステータス",
                "確認済み",1,(short) 1,
                "2026-01-04 23:43:32.502066", "admin",
                "2026-01-04 23:46:35.355428", "admin"));

        // ------------------------------
        // DOMAIN_STATUS（域名状态）
        // ------------------------------
        dictList.add(buildDict(72L, "DOMAIN_STATUS", "ドメインステータス", "デバイスのドメイン参加ステータス",
                "未参加",2,(short) 1,
                "2026-01-04 23:43:32.502320", "admin",
                "2026-01-04 23:46:26.361666", "admin"));
        dictList.add(buildDict(71L, "DOMAIN_STATUS", "ドメインステータス", "デバイスのドメイン参加ステータス",
                "参加済み",1,(short) 1,
                "2026-01-04 23:43:32.502320", "admin",
                "2026-01-04 23:46:28.898138", "admin"));

        // ------------------------------
        // SMARTIT_STATUS（SmartIT状态）
        // ------------------------------
        dictList.add(buildDict(82L, "SMARTIT_STATUS", "SmartITステータス", "デバイスのSmartITインストールステータス",
                "未インストール", 2,(short) 1,
                "2026-01-04 23:43:32.502583", "admin",
                "2026-01-04 23:46:16.805432", "admin"));
        dictList.add(buildDict(81L, "SMARTIT_STATUS", "SmartITステータス", "デバイスのSmartITインストールステータス",
                "インストール済み",1,(short) 1,
                "2026-01-04 23:43:32.502583", "admin",
                "2026-01-04 23:46:22.012964", "admin"));
        
        // ------------------------------
        // USB_STATUS（USB状态）
        // ------------------------------
        dictList.add(buildDict(92L, "USB_STATUS", "USBステータス", "デバイスのUSB使用許可ステータス",
                "禁止", 2, (short) 1,
                "2026-01-04 23:43:32.502878", "admin",
                "2026-01-04 23:46:08.301280", "admin"));
        dictList.add(buildDict(93L, "USB_STATUS", "USBステータス", "デバイスのUSB使用許可ステータス",
                "一時許可", 3, (short) 1,
                "2026-01-04 23:43:32.502878", "admin",
                "2026-01-04 23:46:10.181393", "admin"));
        dictList.add(buildDict(91L, "USB_STATUS", "USBステータス", "デバイスのUSB使用許可ステータス",
                "許可", 1, (short) 1,
                "2026-01-04 23:43:32.502878", "admin",
                "2026-01-04 23:46:12.778059", "admin"));

        // ------------------------------
        // ANTIVIRUS_STATUS（杀毒软件状态）
        // ------------------------------
        dictList.add(buildDict(103L, "ANTIVIRUS_STATUS", "アンチウイルスソフトウェアステータス", "デバイスのアンチウイルスソフトインストールステータス",
                "有効期限切れ", 3, (short) 1,
                "2026-01-04 23:43:32.503189", "admin",
                "2026-01-04 23:45:52.320846", "admin"));
        dictList.add(buildDict(102L, "ANTIVIRUS_STATUS", "アンチウイルスソフトウェアステータス", "デバイスのアンチウイルスソフトインストールステータス",
                "未インストール", 2, (short) 1,
                "2026-01-04 23:43:32.503189", "admin",
                "2026-01-04 23:45:56.912291", "admin"));
        dictList.add(buildDict(101L, "ANTIVIRUS_STATUS", "アンチウイルスソフトウェアステータス", "デバイスのアンチウイルスソフトインストールステータス",
                "インストール済み", 1, (short) 1,
                "2026-01-04 23:43:32.503189", "admin",
                "2026-01-04 23:46:00.037374", "admin"));

        // 执行插入
        dictRepository.saveAll(dictList);
    }

    /**
     * 构建Dict对象（封装重复逻辑）
     */
    private Dict buildDict(Long dictId, String typeCode, String typeName, String typeDesc,
                           String itemName, Integer sort, Short enabled,
                           String createTime, String creater,
                           String updateTime, String updater) {
        Dict dict = new Dict();
        dict.setDictId(dictId);
        dict.setDictTypeCode(typeCode);
        dict.setDictTypeName(typeName);
        dict.setDictTypeDescription(typeDesc);
        dict.setDictItemName(itemName);
        dict.setSort(sort);
        dict.setIsEnabled(enabled);
        dict.setCreateTime(LocalDateTime.parse(createTime, dtFormatter));
        dict.setCreater(creater);
        dict.setUpdateTime(LocalDateTime.parse(updateTime, dtFormatter));
        dict.setUpdater(updater);
        return dict;
    }
}