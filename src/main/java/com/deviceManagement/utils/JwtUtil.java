package com.deviceManagement.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey; // 假设该密钥是URL安全Base64编码格式

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    // Redis黑名单key前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "jwt:blacklist:";

    // 缓存SecretKey
    private SecretKey cachedSecretKey;

    /**
     * 生成SecretKey对象：处理URL安全Base64编码的密钥
     */
    private SecretKey getSigningKey() {
        if (cachedSecretKey == null) {
            synchronized (this) {
                if (cachedSecretKey == null) {
                    try {
                        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
                        if (keyBytes.length < 32) {
                            throw new IllegalStateException("密钥长度不足，HS256算法要求至少32字节");
                        }
                        cachedSecretKey = Keys.hmacShaKeyFor(keyBytes);
                    } catch (Exception e) {
                        log.error("JWT密钥初始化失败", e);
                        throw new RuntimeException("JWT配置错误", e);
                    }
                }
            }
        }
        return cachedSecretKey;
    }

    /**
     * 生成Token：使用SecretKey签名，避免Base64解码异常
     */
    public String generateToken(String userId, Long userTypeId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("员工编号不能为空");
        }
        if (userTypeId == null) {
            throw new IllegalArgumentException("用户类型ID不能为空");
        }
        if (expirationTime <= 0) {
            throw new IllegalStateException("Token过期时间必须大于0");
        }

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userId.trim())
                .claim("userType", userTypeId)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 使用SecretKey签名
                .compact();
    }

    /**
     * 解析Token（私有方法）
     */
    private Claims parseToken(String token) throws JwtException {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token不能为空");
        }

        token = token.trim();

        // 检查是否在黑名单中
        if (isTokenBlacklisted(token)) {
            throw new JwtException("令牌已被撤销");
        }

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取员工编号
     */
    public String getUserIdFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 获取用户类型ID
     */
    public Long getUserTypeIdFromToken(String token) {
        return parseToken(token).get("userType", Long.class);
    }

    /**
     * 验证Token（增强异常日志）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期：{}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Token签名无效：{}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Token格式错误：{}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Token验证失败：{}", e.getMessage());
            return false;
        }
    }

    // ==================== Redis黑名单功能 ====================
    /**
     * 使令牌失效（加入黑名单）
     * 在用户登出或需要强制令牌失效时调用
     */
    public void invalidateToken(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long ttl = expiration.getTime() - System.currentTimeMillis();

            // 只对未过期的令牌加入黑名单
            if (ttl > 0) {
                addToBlacklist(token, ttl);
                log.debug("令牌已加入黑名单，剩余有效期: {}秒", ttl / 1000);
            }
        } catch (JwtException e) {
            log.warn("令牌失效操作失败，令牌可能已过期或无效: {}", e.getMessage());
            // 如果令牌已过期或无效，无需加入黑名单
        }
    }

    /**
     * 将令牌加入黑名单（私有方法）
     */
    private void addToBlacklist(String token, long ttlMillis) {
        String tokenHash = generateTokenHash(token);
        String redisKey = TOKEN_BLACKLIST_PREFIX + tokenHash;

        redisTemplate.opsForValue().set(
                redisKey,
                "1",  // 值不重要，只需要key存在即可
                ttlMillis,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 检查令牌是否在黑名单中
     */
    private boolean isTokenBlacklisted(String token) {
        String tokenHash = generateTokenHash(token);
        String redisKey = TOKEN_BLACKLIST_PREFIX + tokenHash;

        Boolean exists = redisTemplate.hasKey(redisKey);
        return Boolean.TRUE.equals(exists);
    }


    /**
     * 获取令牌剩余有效期（单位：秒）
     * 新增的实用方法
     */
    public long getTokenTTL(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining / 1000); // 返回剩余秒数，不小于0
        } catch (Exception e) {
            return 0; // 如果令牌无效，返回0
        }
    }

    /**
     * 生成令牌哈希（简单实现，避免存储原始token）
     * 生产环境可以考虑使用更安全的哈希算法
     */
    private String generateTokenHash(String token) {
        return Integer.toHexString(token.hashCode());
    }
}