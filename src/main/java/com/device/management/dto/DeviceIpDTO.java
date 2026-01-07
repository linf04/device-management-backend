package com.device.management.dto;


import com.device.management.entity.Device;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceIpDTO {

    private Integer ipId; //IP番号
    private String ipAddress; //IPアドレス
    private String deviceId; //機器番号
    private LocalDateTime createTime; //作成日時
    private String creater; //作成者
    private LocalDateTime updateTime; //更新日時
    private String updater; //更新者
}
