package com.deviceManagement.controller;

import com.deviceManagement.dto.ChangePasswordRequest;
import com.deviceManagement.dto.ChangePasswordResponse;
import com.deviceManagement.dto.LoginRequest;
import com.deviceManagement.dto.LoginResponse;
import com.deviceManagement.common.Result;
import com.deviceManagement.dto.LogoutResponse;
import com.deviceManagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证授权Controller：处理登录、用户信息查询接口
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录接口
     * @param loginRequest 登录请求参数（员工编号+密码）
     * @return Result<String>：成功返回Token，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    /**
     * パスワード変更（ログイン後）
     * @param req
     * @param authHeader
     * @return
     */
    @PostMapping("/change-password")
    public Result<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            @RequestHeader("Authorization") String authHeader) {

        return authService.changePassword(req, authHeader);
    }

    /**
     * 用户登出接口
     * @return Result<Void>：登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return authService.logout();
    }
}