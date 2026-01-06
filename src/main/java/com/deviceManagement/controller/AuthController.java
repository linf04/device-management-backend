package com.deviceManagement.controller;

import com.deviceManagement.common.ApiResponseCode;
import com.deviceManagement.dto.ChangePasswordRequest;
import com.deviceManagement.dto.ChangePasswordResponse;
import com.deviceManagement.dto.LoginRequest;
import com.deviceManagement.dto.LoginResponse;
import com.deviceManagement.dto.ApiResponse;
import com.deviceManagement.exception.BusinessException;
import com.deviceManagement.service.AuthService;
import com.deviceManagement.security.CryptoUtil;
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