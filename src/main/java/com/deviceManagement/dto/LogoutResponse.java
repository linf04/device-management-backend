package com.deviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登出响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponse {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;
}
