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
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "新パスワードは8文字以上で英字・数字・特殊文字を含む必要があります")
    private String newPassword;
}