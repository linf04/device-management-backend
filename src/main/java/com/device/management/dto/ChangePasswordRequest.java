package com.device.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * パスワード変更のリクエスト
 */
@Data
public class ChangePasswordRequest {
    @NotBlank
    private String userId;

    @NotBlank(message = "現在のパスワード（暗号化）は必須です")
    private String currentPassword;

    @NotBlank(message = "新しいパスワード（暗号化）は必須です")
    private String newPassword;
}
