package com.device.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 認証フィルター
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ログインインターフェースの場合、直接通す
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/auth/login") || requestURI.equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            // tokenがあるかチェック
            if (!StringUtils.hasText(jwt)) {
                // tokenがない場合、匿名アクセスが許可されているインターフェースかどうかをチェック
                if (isPermittedUrl(requestURI)) {
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    // 認証が必要だがtokenがない場合、401を返す
                    log.warn("No JWT token found for protected endpoint: {}", requestURI);
                    sendErrorResponse(response, 40100, "トークンが無効または期限切れです");
                    return;
                }
            }

            // tokenを検証
            if (jwtTokenProvider.validateToken(jwt)) {
                String userId = jwtTokenProvider.getUserIdFromToken(jwt);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("User authenticated: {}", userId);

                // フィルター連鎖を続行
                filterChain.doFilter(request, response);
            } else {
                // token検証に失敗
                log.warn("Token validation failed");
                sendErrorResponse(response, 40100, "トークンが無効または期限切れです");
            }

        } catch (io.jsonwebtoken.security.SignatureException e) {
            // Token署名が無効
            log.warn("JWT signature invalid: {}", e.getMessage());
            sendErrorResponse(response, 40102, "トークンの署名が無効です");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Tokenの有効期限切れ
            log.warn("JWT token expired: {}", e.getMessage());
            sendErrorResponse(response, 401, "トークンの有効期限が切れています");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            // Tokenフォーマットエラー
            log.warn("JWT malformed: {}", e.getMessage());
            sendErrorResponse(response, 40100, "トークンが無効または期限切れです");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            // サポートされていないTokenタイプ
            log.warn("Unsupported JWT: {}", e.getMessage());
            sendErrorResponse(response, 40100, "サポートされていないトークンです");
        } catch (IllegalArgumentException e) {
            // Tokenが空
            log.warn("JWT token is empty: {}", e.getMessage());
            sendErrorResponse(response, 40100, "トークンが無効または期限切れです");
        } catch (Exception ex) {
            // その他の例外
            log.error("Could not set user authentication in security context", ex);
            sendErrorResponse(response, 50001, "サーバーが混雑しています。しばらくしてから再度お試しください");
        }
    }

    /**
     * リクエストヘッダーから JWT トークンを取得
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 匿名アクセスが許可されたURLかどうかを判断
     */
    private boolean isPermittedUrl(String requestURI) {
        // ここに匿名アクセスを許可するURLを追加
        return requestURI.equals("/api/auth/logout") ||
                requestURI.equals("/auth/logout") ||
                requestURI.equals("/api/auth/register") ||
                requestURI.equals("/auth/register");
    }

    /**
     * エラーレスポンスを送信
     */
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTPステータスコードを401に設定

        // エラーレスポンスボディを構築
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", code);
        errorResponse.put("msg", message);
        errorResponse.put("success", false);
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        // レスポンスを書き戻す
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        log.debug("Sent error response: code={}, message={}", code, message);
    }
}