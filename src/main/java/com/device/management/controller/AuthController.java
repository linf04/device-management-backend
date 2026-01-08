package com.device.management.controller;

import com.device.management.dto.ChangePasswordRequest;
import com.device.management.dto.ChangePasswordResponse;
import com.device.management.dto.LoginRequest;
import com.device.management.dto.LoginDTO;
import com.device.management.dto.ApiResponse;
import com.device.management.exception.UnauthorizedException;
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
//處理認證相關的HTTP請求，包括登入、登出、密碼修改。
public class AuthController {

    private final AuthService authService;
    /** 共通復号ツール */
    private void decryptPasswordFields(Object dto) {
        try {
            if (dto instanceof LoginRequest req) {
                req.setPassword(CryptoUtil.decrypt(req.getPassword())); // 解密登入密碼
                //前端傳遞的是加密後的密碼，需要在後端解密
            }
            if (dto instanceof ChangePasswordRequest req) {
                req.setCurrentPassword(CryptoUtil.decrypt(req.getCurrentPassword()));
                req.setNewPassword(CryptoUtil.decrypt(req.getNewPassword())); // 解密當前密碼
            }
        } catch (Exception e) {
            log.error("暗号化解除失敗", e);
            throw new UnauthorizedException("暗号化パスワードが無効です");
        }
    }

    /* 1. ログイン（密文） */
    @PostMapping("/login")
    public ApiResponse<LoginDTO> login(@RequestBody LoginRequest loginRequest) {
        // 基本的なパラメータ検証
        if (!StringUtils.hasText(loginRequest.getUserId())) {
//            return ApiResponse.error(400, "ユーザーIDは空にできません");
            throw new UnauthorizedException(400, "ユーザーIDは空にできません");
        }
        if (!StringUtils.hasText(loginRequest.getPassword())) {
//            return ApiResponse.error(400, "パスワードは空にできません");
            throw new UnauthorizedException(400, "パスワードは空にできません");
        }
        decryptPasswordFields(loginRequest);
        return authService.login(loginRequest);
    }

    /* 2. パスワード変更（密文） */
    @PostMapping("/change-password")
    public ApiResponse<ChangePasswordResponse> changePassword(
            @RequestBody ChangePasswordRequest req,
            @RequestHeader("Authorization") String authHeader) {//@RequestBody：接收JSON格式的請求體，@RequestHeader：獲取Authorization請求頭
        decryptPasswordFields(req);// 解密密碼字段
        return authService.changePassword(req, authHeader); // 調用服務層，解密後轉發給服務層
    }

    /* 3. ログアウト（パスワード不要 → そのまま） */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return authService.logout();
    }

}