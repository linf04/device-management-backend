package com.device.management.dto;

import lombok.Data;

/**
 * パスワード変更のリクエスト
 */
@Data
public class ChangePasswordRequest {
    private String userId;

    private String currentPassword;

    private String newPassword;
}
