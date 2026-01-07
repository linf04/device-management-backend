package com.device.management.service;

import com.device.management.dto.ChangePasswordRequest;
import com.device.management.dto.ChangePasswordResponse;
import com.device.management.dto.LoginRequest;
import com.device.management.dto.LoginResponse;
import com.device.management.dto.ApiResponse;

public interface AuthService {
    /**
     * ログイン機能
     * @param loginRequest
     * @return
     */
    ApiResponse<LoginResponse> login(LoginRequest loginRequest);

    /**
     * ユーザーログアウト
     * @return Result<Void>：ログアウト結果
     */
    ApiResponse<Void> logout();



    /**
     * パスワード変更機能
     * @param req
     * @param authHeader
     * @return
     */
    ApiResponse<ChangePasswordResponse> changePassword(ChangePasswordRequest req, String authHeader);
}