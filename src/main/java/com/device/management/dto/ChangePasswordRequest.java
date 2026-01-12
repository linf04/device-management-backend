package com.device.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * パスワード変更のリクエスト
 */
@Data
public class ChangePasswordRequest {
    @NotBlank
    private String userId;

    private String currentPassword;

    private String newPassword;
}
