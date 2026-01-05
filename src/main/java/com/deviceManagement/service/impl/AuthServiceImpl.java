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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DictRepository dictRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest httpServletRequest;

    /**
     * 用户登录：返回包含token和userInfo的登录结果
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

    /**
     * 用户登出
     * 要求必须有有效的token才能登出
     */
    @Override
    public Result<Void> logout() {
        try {
            // 1.从请求头中提取令牌
            String token = extractTokenFromRequest();

            // 2.检查是否提供了token
            if (!StringUtils.hasText(token)) {
                log.warn("nmtoken");
                return Result.error(ResultCode.UNAUTHORIZED, "nmtoken");
            }

            String userId = null;
            // 3.获取用户信息
            try {
                userId = jwtUtil.getUserIdFromToken(token);
                log.info("成功从Token中提取用户ID: {}", userId);
            } catch (Exception e) {
                log.error("从token中提取用户ID失败: {}", e.getMessage());
                return Result.error(ResultCode.UNAUTHORIZED, "token错误");
            }
            // 4.使令牌失效（加入Redis黑名单）
            jwtUtil.invalidateToken(token);
            log.info("Token已加入黑名单，用户ID: {}", userId);

            // 5.清理安全上下文
            cleanupSecurityContext();

            // 6.返回成功响应
            return Result.logoutSuccess();

        } catch (Exception e) {
            log.error("登出处理中发生异常: {}", e.getMessage(), e);
            // 无论如何都清理安全上下文
            cleanupSecurityContext();
            return Result.error(ResultCode.FAIL, "登出失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ChangePasswordResponse> changePassword(ChangePasswordRequest req, String authHeader) {
        return null;
    }

    /**
     * 从请求头中提取令牌
     */
    private String extractTokenFromRequest() {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        log.debug("原始Authorization头: {}", bearerToken);

        if (StringUtils.hasText(bearerToken)) {
            // 打印bearerToken的长度和内容（注意可能包含不可见字符）
            log.debug("Authorization头长度: {}", bearerToken.length());
            for (int i = 0; i < bearerToken.length(); i++) {
                char c = bearerToken.charAt(i);
                log.trace("字符[{}]: {} (ASCII: {})", i, c, (int) c);
            }

            // 检查是否以Bearer开头（不区分大小写）
            if (bearerToken.length() > 7 && bearerToken.substring(0, 7).equalsIgnoreCase("Bearer ")) {
                String token = bearerToken.substring(7).trim();
                log.debug("提取的token: {}... (长度: {})", token.substring(0, Math.min(30, token.length())), token.length());
                return token;
            } else {
                log.warn("Authorization头不以Bearer开头，或者长度不足");
            }
        } else {
            log.warn("Authorization头为空或不存在");
        }
        return null;
    }
    /**
     * 清理安全上下文
     */
    private void cleanupSecurityContext() {
        try {
            SecurityContextHolder.clearContext();
            log.debug("安全上下文已清理");
        } catch (Exception e) {
            log.warn("清理安全上下文时出错: {}", e.getMessage());
        }
    }
}