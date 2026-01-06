package com.deviceManagement.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "社員番号は必須です。")
    private String userId;          // 明文

    @NotBlank(message = "パスワード（暗号化）は必須です")
    private String password;        // 密文
}