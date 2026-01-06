package com.deviceManagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 如果是登录接口，直接放行
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/auth/login") || requestURI.equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            // 检查是否有token
            if (!StringUtils.hasText(jwt)) {
                // 没有token，检查是否是允许匿名访问的接口
                if (isPermittedUrl(requestURI)) {
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    // 需要认证但没有token，返回401
                    log.warn("No JWT token found for protected endpoint: {}", requestURI);
                    sendErrorResponse(response, 40100, "トークンが無効または期限切れです");
                    return;
                }
            }

            // 验证token
            if (jwtTokenProvider.validateToken(jwt)) {
                String userId = jwtTokenProvider.getUserIdFromToken(jwt);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("User authenticated: {}", userId);

                // 继续过滤器链
                filterChain.doFilter(request, response);
            } else {
                // token验证失败
                log.warn("Token validation failed");
                sendErrorResponse(response, 40100, "トークンが無効または期限切れです");
            }

        } catch (io.jsonwebtoken.security.SignatureException e) {
            // Token签名无效
            log.warn("JWT signature invalid: {}", e.getMessage());
            sendErrorResponse(response, 40102, "トークンの署名が無効です");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token过期
            log.warn("JWT token expired: {}", e.getMessage());
            sendErrorResponse(response, 401, "トークンの有効期限が切れています");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            // Token格式错误
            log.warn("JWT malformed: {}", e.getMessage());
            sendErrorResponse(response, 40100, "トークンが無効または期限切れです");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            // 不支持的Token类型
            log.warn("Unsupported JWT: {}", e.getMessage());
            sendErrorResponse(response, 40100, "サポートされていないトークンです");
        } catch (IllegalArgumentException e) {
            // Token为空
            log.warn("JWT token is empty: {}", e.getMessage());
            sendErrorResponse(response, 40100, "トークンが無効または期限切れです");
        } catch (Exception ex) {
            // 其他异常
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
     * 判断是否是允许匿名访问的URL
     */
    private boolean isPermittedUrl(String requestURI) {
        // 这里添加允许匿名访问的URL
        return requestURI.equals("/api/auth/logout") ||
                requestURI.equals("/auth/logout") ||
                requestURI.equals("/api/auth/register") ||
                requestURI.equals("/auth/register");
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置 HTTP 状态码为 401

        // 构建错误响应体
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", code);
        errorResponse.put("msg", message);
        errorResponse.put("success", false);
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        // 写回响应
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        log.debug("Sent error response: code={}, message={}", code, message);
    }
}