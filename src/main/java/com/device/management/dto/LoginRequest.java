package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ログインリクエスト
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String userId;
    private String password;
}
