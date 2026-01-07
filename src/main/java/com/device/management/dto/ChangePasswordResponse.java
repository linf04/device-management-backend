package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * パスワード変更の応答
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {
    private Integer code;
    private String msg;
}