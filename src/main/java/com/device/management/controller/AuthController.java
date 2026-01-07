package com.device.management.controller;

import com.device.management.common.ApiResponseCode;
import com.device.management.dto.ChangePasswordRequest;
import com.device.management.dto.ChangePasswordResponse;
import com.device.management.dto.LoginRequest;
import com.device.management.dto.LoginResponse;
import com.device.management.dto.ApiResponse;
import com.device.management.exception.BusinessException;
import com.device.management.service.AuthService;
import com.device.management.security.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 認証Controller：ログイン、ユーザー情報インターフェースを処理する
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    /** 共通復号ツール */
    private void decryptPasswordFields(Object dto) {
        try {
            if (dto instanceof LoginRequest req) {
                req.setPassword(CryptoUtil.decrypt(req.getPassword()));
            }
            if (dto instanceof ChangePasswordRequest req) {
                req.setCurrentPassword(CryptoUtil.decrypt(req.getCurrentPassword()));
                req.setNewPassword(CryptoUtil.decrypt(req.getNewPassword()));
            }
        } catch (Exception e) {
            log.error("暗号化解除失敗", e);
            throw new BusinessException(ApiResponseCode.PARAM_ERROR, "暗号化パスワードが無効です");
        }
    }

    /* 1. ログイン（密文） */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 基本的なパラメータ検証
        if (!StringUtils.hasText(loginRequest.getUserId())) {
            return ApiResponse.error(ApiResponseCode.PARAM_ERROR, "ユーザーIDは空にできません");
        }
        if (!StringUtils.hasText(loginRequest.getPassword())) {
            return ApiResponse.error(ApiResponseCode.PARAM_ERROR, "パスワードは空にできません");
        }
        decryptPasswordFields(loginRequest);
        return authService.login(loginRequest);
    }

    /* 2. パスワード変更（密文） */
    @PostMapping("/change-password")
    public ApiResponse<ChangePasswordResponse> changePassword(
            @RequestBody ChangePasswordRequest req,
            @RequestHeader("Authorization") String authHeader) {
        decryptPasswordFields(req);
        return authService.changePassword(req, authHeader);
    }

    /* 3. ログアウト（パスワード不要 → そのまま） */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return authService.logout();
    }

}