package com.device.management.service.impl;

import com.device.management.dto.*;
import com.device.management.exception.AllException;
import com.device.management.exception.UnauthorizedException;
import com.device.management.repository.DictRepository;
import com.device.management.repository.UserRepository;
import com.device.management.dto.ApiResponse;
import com.device.management.entity.Dict;
import com.device.management.entity.User;
import com.device.management.service.AuthService;
import com.device.management.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.Objects;



@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DictRepository dictRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest httpServletRequest;
    /**
     * ユーザーログイン：tokenとuserInfoを含むログイン結果を返す
     * @param loginRequest ログインリクエストパラメータ
     * @return Result<LoginResponse>：成功時はtoken+ユーザー情報、失敗時はエラー列挙型を返す
     */
    @Override
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        // 1. ユーザーIDでユーザーを検索（存在しない場合は例外をスロー、ResultCode列挙型を直接使用して構築）
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new UnauthorizedException("ユーザーが存在しません"));

        // 2. パスワードを検証（失敗時は直接パスワードエラー列挙型を返す）
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(401, "パスワードが正しくありません");
        }

        // 3. JWT Tokenを生成
        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUserTypeId());

        // 4. UserInfoを構築する際にユーザータイプを検索（存在しない場合はシステムエラーをスロー）
        Dict userType = dictRepository.findByDictIdAndDictTypeCode(
                user.getUserTypeId(),
                "USER_TYPE"
        ).orElseThrow(() -> new AllException( "システムエラー"));


        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setDeptId(user.getDeptId());
        userDTO.setName(user.getName());
        userDTO.setUserTypeName(userType.getDictItemName());

        // 5. LoginResponseを組み立て
        LoginResponse loginResponse = new LoginResponse(token, userDTO);

        // 6. ログイン成功結果を返す（ResultのloginSuccess静的ファクトリメソッドを使用）
        return ApiResponse.success( "ログイン成功",loginResponse);
    }

    /**
     * ユーザーログアウト
     * 有効なtokenがなければログアウトできないことを要求
     */
    @Override
    public ApiResponse<Void> logout() {
        try {
            // 1.リクエストヘッダーからトークンを抽出
            String token = extractTokenFromRequest();

            // 2.tokenが提供されているかチェック
            if (!StringUtils.hasText(token)) {
                log.warn("nmtoken");
                cleanupSecurityContext();
                return ApiResponse.error(401, "nmtoken");
            }

            String userId = null;
            // 3.ユーザー情報を取得
            try {
                userId = jwtTokenProvider.getUserIdFromToken(token);
                log.info("トークンからユーザーIDを抽出成功: {}", userId);
            } catch (Exception e) {
                log.error("トークンからユーザーIDの抽出に失敗: {}", e.getMessage());
                return ApiResponse.error(401, "トークンエラー");
            }
            // 4.トークンを無効化（Redisブラックリストに追加）
            jwtTokenProvider.invalidateToken(token);
            log.info("トークンがブラックリストに追加されました、ユーザーID: {}", userId);

            // 5.セキュリティコンテキストをクリーンアップ
            cleanupSecurityContext();

            // 6.成功レスポンスを返す
            return ApiResponse.success( "ログアウト成功");

        } catch (Exception e) {
            log.error("ログアウト処理中に例外が発生: {}", e.getMessage(), e);
            // 何があってもセキュリティコンテキストをクリーンアップ
            cleanupSecurityContext();
            return ApiResponse.error(500, "ログアウト失敗: " + e.getMessage());
        }
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
        }
    }
    /**
     * パスワード変更
     *・一般ユーザ：自分のパスワードのみ変更可能（旧パスワード必須）
     *・管理者　　：全ユーザーのパスワードをリセット可能（旧パスワード不要）
     * @param req  変更内容（userId / currentPassword / newPassword）
     * @param authHeader Authorization: Bearer <JWT>
     * @return Result<ChangePasswordResponse> 成功時 20000, 失敗時各業務エラーコード
     * @throws UnauthorizedException システムエラー（ユーザ不在等）
     */
    @Override
    @Transactional
    public ApiResponse<ChangePasswordResponse> changePassword(ChangePasswordRequest req,
                                                              String authHeader) {
        // 1.JWT を解析し、検証する
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        if (!jwtTokenProvider.validateToken(token)) {
            return ApiResponse.error(401, "トークンが無効です");
        }
        String tokenUserId = jwtTokenProvider.getUserIdFromToken(token);
        Long tokenUserType = jwtTokenProvider.getUserTypeIdFromToken(token);

        // 2. 一般ユーザーは自身のパスワードのみ変更可能 管理者は全ユーザーのパスワードを変更可能
        boolean isAdmin = Objects.equals(tokenUserType, 11L);
        if (!isAdmin && !tokenUserId.equals(req.getUserId())) {
            return ApiResponse.error(403, "他人のパスワードを変更する権限がありません");
        }

        // 3. 旧パスワードを検証（管理者はスキップ）
        User user = userRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new UnauthorizedException("ユーザーが存在しません"));

        if (!isAdmin && !passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            return ApiResponse.error(40001, "現在のパスワードが正しくありません");
        }

        // 4. 新しいパスワードは古いパスワードと同一にできません
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            return ApiResponse.error(40003, "新しいパスワードは古いパスワードと同じにすることはできません");
        }

        // 5. パスワード強度の二重チェック
        if (!req.getNewPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?])[A-Za-z\\d@$!%*?]{8,}$")) {
            return ApiResponse.error(40002, "新しいパスワードが強度要件を満たしていません");
        }

        // 6. パスワードを更新する
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        // 7. レスポンスを返却する
        ChangePasswordResponse resp = new ChangePasswordResponse();
        resp.setCode(20000);
        resp.setMsg( "パスワードが更新されました。再度ログインしてください。");
        return ApiResponse.success(resp);
    }

}