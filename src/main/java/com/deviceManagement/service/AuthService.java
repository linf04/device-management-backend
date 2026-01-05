package com.deviceManagement.service;

import com.deviceManagement.dto.ChangePasswordRequest;
import com.deviceManagement.dto.ChangePasswordResponse;
import com.deviceManagement.dto.LoginRequest;
import com.deviceManagement.dto.LoginResponse;
import com.deviceManagement.common.Result;
import com.deviceManagement.dto.LogoutResponse;

public interface AuthService {
    /**
     * 登录功能
     * @param loginRequest
     * @return
     */
    Result<LoginResponse> login(LoginRequest loginRequest);

    /**
     * 用户登出
     * @return Result<Void>：登出结果
     */
    Result<Void> logout();



    /**
     * パスワード変更機能
     * @param req
     * @param authHeader
     * @return
     */
    Result<ChangePasswordResponse> changePassword(ChangePasswordRequest req, String authHeader);
}
