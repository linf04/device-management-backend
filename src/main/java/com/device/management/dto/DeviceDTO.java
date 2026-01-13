package com.device.management.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class DeviceDTO {

    private String deviceId; //機器番号
    private String deviceModel; //ホストモデル
    private String computerName; //コンピュータ名
    private String loginUsername; //ログインユーザ名
    private String project; //所属プロジェクト
    private String devRoom; //所属開発室
    private String userId; //従業員番号
    private UserDto userInfo;   //ユーザ情報
    private String remark; //備考

    private Integer selfConfirmId; //本人確認ID
    private Long osId; //OSID
    private Long memoryId; //メモリID
    private Long ssdId; //SSDID
    private Long hddId; //HDDID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; //作成日時
    private String creater; //作成者
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; //更新日時
    private String updater; //更新者

    //辞書
    @JsonProperty("selfConfirm")
    private DictDTO selfConfirmDict;//本人確認辞書項目
    @JsonProperty("os")
    private DictDTO osDict;//オペレーティングシステム辞書項目
    @JsonProperty("memory")
    private DictDTO memoryDict;//メモリ辞書項目
    @JsonProperty("ssd")
    private DictDTO ssdDict;//SSD辞書項目
    @JsonProperty("hdd")
    private DictDTO hddDict;//HDD辞書項目


    //関連情報
    @JsonProperty("monitors")
    @Builder.Default
    private List<MonitorDTO> monitors = new ArrayList<>();//モニターリスト
    @JsonProperty("deviceIps")
    @Builder.Default
    private List<DeviceIpDTO> deviceIps = new ArrayList<>();//IPアドレスリスト

}
