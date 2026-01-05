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
<<<<<<< HEAD
     * ユーザーログイン：tokenとuserInfoを含むログイン結果を返す
     * @param loginRequest ログインリクエストパラメータ
     * @return Result<LoginResponse>：成功時はtoken+ユーザー情報、失敗時はエラー列挙型を返す
=======
     * 用户登录：返回包含token和userInfo的登录结果
     *
     * @param loginRequest 登录请求参数
     * @return Result<LoginResponse>：成功返回token+用户信息，失败返回错误枚举
>>>>>>> 20ef5d44e27be8928c63249c7558ae539c99f808
     */
    @Override
    public Result<LoginResponse> login(LoginRequest loginRequest) {
        // 1. ユーザーIDでユーザーを検索（存在しない場合は例外をスロー、ResultCode列挙型を直接使用して構築）
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        // 2. パスワードを検証（失敗時は直接パスワードエラー列挙型を返す）
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 3. JWT Tokenを生成
        String token = jwtUtil.generateToken(user.getUserId(), user.getUserTypeId());

        // 4. UserInfoを構築する際にユーザータイプを検索（存在しない場合はシステムエラーをスロー）
        Dict userType = dictRepository.findByDictIdAndDictTypeCode(
                user.getUserTypeId(),
                "USER_TYPE"
        ).orElseThrow(() -> new BusinessException(ResultCode.FAIL));


        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setName(user.getName());
        userInfo.setUserTypeName(userType.getDictItemName());

        // 5. LoginResponseを組み立て
        LoginResponse loginResponse = new LoginResponse(token, userInfo);

        // 6. ログイン成功結果を返す（ResultのloginSuccess静的ファクトリメソッドを使用）
        return Result.loginSuccess(loginResponse);
    }

<<<<<<< HEAD
    /**
     * ユーザーログアウト
     * 有効なtokenがなければログアウトできないことを要求
     */
    @Override
    public Result<Void> logout() {
        try {
            // 1.リクエストヘッダーからトークンを抽出
            String token = extractTokenFromRequest();

            // 2.tokenが提供されているかチェック
            if (!StringUtils.hasText(token)) {
                log.warn("nmtoken");
                return Result.error(ResultCode.UNAUTHORIZED, "nmtoken");
            }

            String userId = null;
            // 3.ユーザー情報を取得
            try {
                userId = jwtUtil.getUserIdFromToken(token);
                log.info("トークンからユーザーIDを抽出成功: {}", userId);
            } catch (Exception e) {
                log.error("トークンからユーザーIDの抽出に失敗: {}", e.getMessage());
                return Result.error(ResultCode.UNAUTHORIZED, "トークンエラー");
            }
            // 4.トークンを無効化（Redisブラックリストに追加）
            jwtUtil.invalidateToken(token);
            log.info("トークンがブラックリストに追加されました、ユーザーID: {}", userId);

            // 5.セキュリティコンテキストをクリーンアップ
            cleanupSecurityContext();

            // 6.成功レスポンスを返す
            return Result.logoutSuccess();

        } catch (Exception e) {
            log.error("ログアウト処理中に例外が発生: {}", e.getMessage(), e);
            // 何があってもセキュリティコンテキストをクリーンアップ
            cleanupSecurityContext();
            return Result.error(ResultCode.FAIL, "ログアウト失敗: " + e.getMessage());
        }
=======
    @Override
    public Result<Void> logout() {
        return null;
>>>>>>> 20ef5d44e27be8928c63249c7558ae539c99f808
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
<<<<<<< HEAD
    public Result<ChangePasswordResponse> changePassword(ChangePasswordRequest req, String authHeader) {
        return null;
    }

    /**
     * リクエストヘッダーからトークンを抽出
     */
    private String extractTokenFromRequest() {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        log.debug("元のAuthorizationヘッダー: {}", bearerToken);

        if (StringUtils.hasText(bearerToken)) {
            // bearerTokenの長さと内容を表示（不可視文字を含む可能性があることに注意）
            log.debug("Authorizationヘッダー長さ: {}", bearerToken.length());
            for (int i = 0; i < bearerToken.length(); i++) {
                char c = bearerToken.charAt(i);
                log.trace("文字[{}]: {} (ASCII: {})", i, c, (int) c);
            }

            // Bearerで始まるかチェック（大文字小文字を区別しない）
            if (bearerToken.length() > 7 && bearerToken.substring(0, 7).equalsIgnoreCase("Bearer ")) {
                String token = bearerToken.substring(7).trim();
                log.debug("抽出されたトークン: {}... (長さ: {})", token.substring(0, Math.min(30, token.length())), token.length());
                return token;
            } else {
                log.warn("AuthorizationヘッダーがBearerで始まらない、または長さが不足");
            }
        } else {
            log.warn("Authorizationヘッダーが空または存在しない");
        }
        return null;
    }
    /**
     * セキュリティコンテキストをクリーンアップ
     */
    private void cleanupSecurityContext() {
        try {
            SecurityContextHolder.clearContext();
            log.debug("セキュリティコンテキストをクリーンアップしました");
        } catch (Exception e) {
            log.warn("セキュリティコンテキストのクリーンアップ中にエラーが発生: {}", e.getMessage());
=======
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
>>>>>>> 20ef5d44e27be8928c63249c7558ae539c99f808
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