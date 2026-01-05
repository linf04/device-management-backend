package com.deviceManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * パスワード変更のリクエスト
 */
@Data
public class ChangePasswordRequest {
    @NotBlank(message = "userIdは必須です")
    private String userId;

    @NotBlank(message = "現在のパスワードは必須です")
    private String currentPassword;

    @NotBlank(message = "新しいパスワードは必須です")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "新しいパスワードは8文字以上で、英字・数字・特殊文字（@$!%*?&）をそれぞれ1文字以上含める必要があります。")
    private String newPassword;
}