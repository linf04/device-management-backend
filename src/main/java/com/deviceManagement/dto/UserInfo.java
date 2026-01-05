package com.deviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserInfo {
    private String userId;
    private String deptId;
    private String name;
    private String userTypeName; // 用户类型名称
}
