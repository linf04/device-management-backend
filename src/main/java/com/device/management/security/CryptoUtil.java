package com.device.management.security;

import com.device.management.dto.ChangePasswordRequest;
import com.device.management.dto.LoginRequest;
import com.device.management.exception.DecryptionException;
import com.device.management.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class CryptoUtil {
    private static final String KEY = "1234567890abcdef";
    private static final String ALG  = "AES/ECB/PKCS5Padding";

    /** 共通復号ツール */
    public static void decryptPasswordFields(Object dto) {
        try {
            if (dto instanceof LoginRequest req) {
                // passwordがnullの場合はエラー
                if (req.getPassword() == null) {
                    throw new UnauthorizedException("パスワードは必須です");
                }
                req.setPassword(CryptoUtil.decrypt(req.getPassword())); // ログインパスワードを復号
                // フロントエンドから送信されるのは暗号化されたパスワードで、バックエンドで復号する必要があります
            }
            if (dto instanceof ChangePasswordRequest req) {
                // currentPasswordがnullの場合はスキップ（管理者がパスワードを変更する場合）
                if (req.getCurrentPassword() != null) {
                    req.setCurrentPassword(CryptoUtil.decrypt(req.getCurrentPassword()));
                }
                // newPasswordがnullの場合はエラー（必須項目）
                if (req.getNewPassword() == null) {
                    throw new UnauthorizedException("新しいパスワード（暗号化）は必須です");
                }
                req.setNewPassword(CryptoUtil.decrypt(req.getNewPassword())); // 新しいパスワードを復号
            }
        } catch (Exception e) {
            log.error("暗号化解除失敗", e);
            throw new UnauthorizedException("暗号化パスワードが無効です");
        }
    }

    public static String decrypt(String base64) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // Base64デコード失敗例外をキャッチ
            throw new DecryptionException("無効な暗号化形式");
        }catch (Exception e) {
            log.error("復号に失敗しました: {}", base64, e);
            throw new DecryptionException("復号に失敗しました");
        }
    }
}