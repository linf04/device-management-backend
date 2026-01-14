package com.device.management.controller;

import com.device.management.dto.ChangePasswordRequest;
import com.device.management.dto.LoginRequest;
import com.device.management.dto.LoginDto;
import com.device.management.dto.ApiResponse;
import com.device.management.service.AuthService;
import com.device.management.security.CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 認証Controller：ログイン、ユーザー情報インターフェースを処理する
 */
@Slf4j
@RestController
@RequestMapping("/auth")
// 認証関連のHTTPリクエストを処理します。ログイン、ログアウト、パスワード変更を含みます。
public class AuthController {

    @Autowired
    private AuthService authService;

    /* 1. ログイン（密文） */
    @PostMapping("/login")
    public ApiResponse<LoginDto> login(@RequestBody LoginRequest loginRequest) {
        CryptoUtil.decryptPasswordFields(loginRequest);// パスワードフィールドを復号
        return authService.login(loginRequest);
    }

    /* 2. パスワード変更（密文） */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest req) {
        CryptoUtil.decryptPasswordFields(req);// パスワードフィールドを復号
        return authService.changePassword(req); // サービス層を呼び出し、復号後サービス層に転送
    }

    /* 3. ログアウト（パスワード不要 → そのまま） */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return authService.logout();
    }

}