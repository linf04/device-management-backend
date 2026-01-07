package com.device.management.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // Redisブラックリストキープレフィックス
    private static final String TOKEN_BLACKLIST_PREFIX = "jwt:blacklist:";

    // SecretKeyをキャッシュ
    private SecretKey cachedSecretKey;

    /**
     * SecretKeyオブジェクトを生成：URL安全なBase64エンコードされた鍵を処理
     */
    private SecretKey getSigningKey() {
        if (cachedSecretKey == null) {
            synchronized (this) {
                if (cachedSecretKey == null) {
                    try {
                        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
                        if (keyBytes.length < 32) {
                            throw new IllegalStateException("鍵の長さが不足しています。HS256アルゴリズムは少なくとも32バイト必要です");
                        }
                        cachedSecretKey = Keys.hmacShaKeyFor(keyBytes);
                    } catch (Exception e) {
                        log.error("JWT鍵の初期化に失敗しました", e);
                        throw new RuntimeException("JWT設定エラー", e);
                    }
                }
            }
        }
        return cachedSecretKey;
    }

    /**
     * Tokenを生成：SecretKeyで署名し、Base64デコード例外を回避
     */
    public String generateToken(String userId, Long userTypeId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("従業員番号は空にできません");
        }
        if (userTypeId == null) {
            throw new IllegalArgumentException("ユーザータイプIDは空にできません");
        }
        if (expirationTime <= 0) {
            throw new IllegalStateException("Tokenの有効期限は0より大きくなければなりません");
        }

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userId.trim())
                .claim("userType", userTypeId)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // SecretKeyで署名
                .compact();
    }

    /**
     * Tokenを解析（プライベートメソッド）
     */
    private Claims parseToken(String token) throws JwtException {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Tokenは空にできません");
        }

        token = token.trim();

        // ブラックリストに含まれているかチェック
        if (isTokenBlacklisted(token)) {
            throw new JwtException("トークンは既に取り消されています");
        }

/*        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();*/

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    /**
     * 従業員番号を取得
     */
    public String getUserIdFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * ユーザータイプIDを取得
     */
    public Long getUserTypeIdFromToken(String token) {
        return parseToken(token).get("userType", Long.class);
    }

    /**
     * Tokenを検証（例外ログを強化）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Tokenの有効期限が切れています：{}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Tokenの署名が無効です：{}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Tokenフォーマットエラー：{}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Token検証に失敗しました：{}", e.getMessage());
            return false;
        }
    }

    // ==================== Redisブラックリスト機能 ====================
    /**
     * トークンを無効化（ブラックリストに追加）
     * ユーザーログアウト時やトークンの強制無効化が必要なときに呼び出します
     */
    public void invalidateToken(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long ttl = expiration.getTime() - System.currentTimeMillis();

            // 有効期限が切れていないトークンのみブラックリストに追加
            if (ttl > 0) {
                addToBlacklist(token, ttl);
                log.debug("トークンがブラックリストに追加されました、残り有効期間: {}秒", ttl / 1000);
            }
        } catch (JwtException e) {
            log.warn("トークン無効化操作に失敗しました、トークンは既に期限切れまたは無効の可能性があります: {}", e.getMessage());
            // トークンが既に期限切れまたは無効の場合は、ブラックリストに追加する必要はありません
        }
    }

    /**
     * トークンをブラックリストに追加（プライベートメソッド）
     */
    private void addToBlacklist(String token, long ttlMillis) {
        String tokenHash = generateTokenHash(token);
        String redisKey = TOKEN_BLACKLIST_PREFIX + tokenHash;

        redisTemplate.opsForValue().set(
                redisKey,
                "1",  // 値は重要ではなく、キーが存在するだけで十分です
                ttlMillis,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * トークンがブラックリストに含まれているかチェック
     */
    private boolean isTokenBlacklisted(String token) {
        String tokenHash = generateTokenHash(token);
        String redisKey = TOKEN_BLACKLIST_PREFIX + tokenHash;

        Boolean exists = redisTemplate.hasKey(redisKey);
        return Boolean.TRUE.equals(exists);
    }


    /**
     * トークンの残り有効期間を取得（単位：秒）
     * 新しく追加された実用的なメソッド
     */
    public long getTokenTTL(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining / 1000); // 残り秒数を返します。0未満にはなりません
        } catch (Exception e) {
            return 0; // トークンが無効の場合は0を返します
        }
    }

    /**
     * トークンハッシュを生成（シンプルな実装、元のtokenの保存を回避）
     * 本番環境ではより安全なハッシュアルゴリズムの使用を検討できます
     */
    private String generateTokenHash(String token) {
        return Integer.toHexString(token.hashCode());
    }
}