package com.device.management.dto;

import com.device.management.entity.DeviceIp;
import com.device.management.entity.Monitor;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceFullDTO {

    // device
    private String deviceId; //機器番号
    private String deviceModel; //ホストモデル
    private String computerName; //コンピュータ名
    private String loginUsername; //ログインユーザ名
    private String project; //所属プロジェクト
    private String devRoom; //所属開発室
    private String userId; //従業員番号
    private String remark; //備考
    private Integer selfConfirmId; //本人確認ID
    private Long osId; //OSID
    private Long memoryId; //メモリID
    private Long ssdId; //SSDID
    private Long hddId; //HDDID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 作成日時
    private String creater; // 作成者

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 更新日時
    private String updater; // 更新者

    // monitor - 内部保存フィールド
    @JsonIgnore
    @Builder.Default
    private List<Monitor> monitorsInput = new ArrayList<>(); // リクエスト受信時のモニター一覧

    //ip - 内部保存フィールド
    @JsonIgnore
    @Builder.Default
    private List<DeviceIp> deviceIpsInput = new ArrayList<>(); // リクエスト受信時のIPアドレス一覧

    /**
     * シンプル化されたモニター一覧（名前のみ）- 返信用
     */
    @JsonGetter("monitors")
    public List<MonitorSimple> getMonitors() {
        if (monitorsInput == null || monitorsInput.isEmpty()) {
            return new ArrayList<>();
        }
        return monitorsInput.stream()
                .map(m -> new MonitorSimple(m.getMonitorName()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * モニター一覧を設定 - リクエスト受信用
     */
    @JsonSetter("monitors")
    public void setMonitors(List<Monitor> monitors) {
        this.monitorsInput = monitors;
    }

    /**
     * シンプル化されたIPアドレス一覧（IPアドレスのみ）- 返信用
     */
    @JsonGetter("deviceIps")
    public List<DeviceIpSimple> getDeviceIps() {
        if (deviceIpsInput == null || deviceIpsInput.isEmpty()) {
            return new ArrayList<>();
        }
        return deviceIpsInput.stream()
                .map(ip -> new DeviceIpSimple(ip.getIpAddress()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * IPアドレス一覧を設定 - リクエスト受信用
     */
    @JsonSetter("deviceIps")
    public void setDeviceIps(List<DeviceIp> deviceIps) {
        this.deviceIpsInput = deviceIps;
    }

    /**
     * シンプル化されたモニター情報（名前のみ）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonitorSimple {
        private String monitorName;
    }

    /**
     * シンプル化されたIPアドレス情報（IPアドレスのみ）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceIpSimple {
        private String ipAddress;
    }

    // user
    private String name;
    private String deptId;

    // 辞書項目 - 内部保存フィールド
    @JsonIgnore
    private DictDTO selfConfirmDictInput; // 本人確認辞書項目
    @JsonIgnore
    private DictDTO osDictInput; // オペレーティングシステム辞書項目
    @JsonIgnore
    private DictDTO memoryDictInput; // メモリ辞書項目
    @JsonIgnore
    private DictDTO ssdDictInput; // SSD辞書項目
    @JsonIgnore
    private DictDTO hddDictInput; // HDD辞書項目

    /**
     * シンプル化された本人確認辞書（dictItemNameのみ）- 返信用
     */
    @JsonGetter("selfConfirm")
    public DictSimple getSelfConfirm() {
        return selfConfirmDictInput != null ? new DictSimple(selfConfirmDictInput.getDictItemName()) : null;
    }

    /**
     * 本人確認辞書を設定 - 内部処理用
     */
    public void setSelfConfirmDict(DictDTO dict) {
        this.selfConfirmDictInput = dict;
    }

    /**
     * シンプル化されたオペレーティングシステム辞書（dictItemNameのみ）- 返信用
     */
    @JsonGetter("os")
    public DictSimple getOs() {
        return osDictInput != null ? new DictSimple(osDictInput.getDictItemName()) : null;
    }

    /**
     * オペレーティングシステム辞書を設定 - 内部処理用
     */
    public void setOsDict(DictDTO dict) {
        this.osDictInput = dict;
    }

    /**
     * シンプル化されたメモリ辞書（dictItemNameのみ）- 返信用
     */
    @JsonGetter("memory")
    public DictSimple getMemory() {
        return memoryDictInput != null ? new DictSimple(memoryDictInput.getDictItemName()) : null;
    }

    /**
     * メモリ辞書を設定 - 内部処理用
     */
    public void setMemoryDict(DictDTO dict) {
        this.memoryDictInput = dict;
    }

    /**
     * シンプル化されたSSD辞書（dictItemNameのみ）- 返信用
     */
    @JsonGetter("ssd")
    public DictSimple getSsd() {
        return ssdDictInput != null ? new DictSimple(ssdDictInput.getDictItemName()) : null;
    }

    /**
     * SSD辞書を設定 - 内部処理用
     */
    public void setSsdDict(DictDTO dict) {
        this.ssdDictInput = dict;
    }

    /**
     * シンプル化されたHDD辞書（dictItemNameのみ）- 返信用
     */
    @JsonGetter("hdd")
    public DictSimple getHdd() {
        return hddDictInput != null ? new DictSimple(hddDictInput.getDictItemName()) : null;
    }

    /**
     * HDD辞書を設定 - 内部処理用
     */
    public void setHddDict(DictDTO dict) {
        this.hddDictInput = dict;
    }

    /**
     * シンプル化された辞書情報（dictItemNameのみ）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DictSimple {
        private String dictItemName;
    }
}
