package com.deviceManagement.service.impl;

import com.deviceManagement.dto.*;
import com.deviceManagement.repository.DictRepository;
import com.deviceManagement.repository.UserRepository;
import com.deviceManagement.common.Result;
import com.deviceManagement.common.ResultCode;
import com.deviceManagement.entity.Dict;
import com.deviceManagement.entity.User;
import com.deviceManagement.exception.BusinessException;
import com.deviceManagement.service.AuthService;
import com.deviceManagement.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DictRepository dictRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户登录：返回包含token和userInfo的登录结果
     *
     * @param loginRequest 登录请求参数
     * @return Result<LoginResponse>：成功返回token+用户信息，失败返回错误枚举
     */
    @Override
    public Result<LoginResponse> login(LoginRequest loginRequest) {
        // 1. 根据用户ID查询用户（不存在则抛出异常，使用ResultCode枚举直接构造）
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        // 2. 验证密码（失败直接返回密码错误枚举）
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 3. 生成JWT Token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUserTypeId());

        // 4. 构建UserInfo时查询用户类型（不存在则抛出系统错误）
        Dict userType = dictRepository.findByDictIdAndDictTypeCode(
                user.getUserTypeId(),
                "USER_TYPE"
        ).orElseThrow(() -> new BusinessException(ResultCode.FAIL));


        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setName(user.getName());
        userInfo.setUserTypeName(userType.getDictItemName());

        // 5. 组装LoginResponse
        LoginResponse loginResponse = new LoginResponse(token, userInfo);

        // 6. 返回登录成功结果（使用Result的loginSuccess静态工厂方法）
        return Result.loginSuccess(loginResponse);
    }

    @Override
    public Result<Void> logout() {
        return null;
    }


    /**
     * パスワード変更
     * ・一般ユーザ：自分のパスワードのみ変更可能（旧パスワード必須）
     * ・管理者　　：全ユーザーのパスワードをリセット可能（旧パスワード不要）
     *
     * @param req        変更内容（userId / currentPassword / newPassword）
     * @param authHeader
     * @return Result<ChangePasswordResponse> 成功時 20000, 失敗時各業務エラーコード
     * @throws BusinessException システムエラー（ユーザ不在等）
     */
    @Override
    @Transactional
    public Result<ChangePasswordResponse> changePassword(ChangePasswordRequest req,
                                                         String authHeader) {
        // 1. JWT を解析し、検証する
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        if (!jwtUtil.validateToken(token)) {
            return Result.error(ResultCode.TOKEN_INVALID);
        }
        String tokenUserId = jwtUtil.getUserIdFromToken(token);
        Long tokenUserType = jwtUtil.getUserTypeIdFromToken(token);

        // 2. 一般ユーザーは自身のパスワードのみ変更可能 管理者は全ユーザーのパスワードを変更可能
        boolean isAdmin = Objects.equals(tokenUserType, 11L);
        if (!isAdmin && !tokenUserId.equals(req.getUserId())) {
            return Result.error(ResultCode.FORBIDDEN, "他人のパスワードを変更する権限がありません。\n");
        }

        // 3. 旧パスワードを検証（管理者はスキップ）
        User user = userRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        if (!isAdmin && !passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            return Result.error(ResultCode.WRONG_CURRENT_PASSWORD);
        }

        // 4. 新しいパスワードは古いパスワードと同一にできません
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            return Result.error(ResultCode.PASSWORD_SAME_AS_OLD);
        }

        // 5. パスワード強度の二重チェック
        if (!req.getNewPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            return Result.error(ResultCode.WEAK_NEW_PASSWORD);
        }

        // 6. パスワードを更新する
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        // 7. レスポンスを返却する
        ChangePasswordResponse resp = new ChangePasswordResponse();
        resp.setCode(ResultCode.PASSWORD_CHANGED_SUCCESS.getCode());
        resp.setMsg(ResultCode.PASSWORD_CHANGED_SUCCESS.getMessage());
        return Result.passwordChangedSuccess(resp);
    }
}