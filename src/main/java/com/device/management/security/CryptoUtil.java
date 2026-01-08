package com.device.management.security;

import com.device.management.exception.DecryptionException;
import com.device.management.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class CryptoUtil {
    private static final String KEY = "1234567890abcdef";
    private static final String ALG  = "AES/ECB/PKCS5Padding";
    private static final String CHARSET = "UTF-8";

    public static String decrypt(String base64) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // 捕获Base64解码失败异常
            throw new DecryptionException("無効な暗号化形式");
        }catch (Exception e) {
            log.error("復号に失敗しました: {}", base64, e);
            throw new DecryptionException("復号に失敗しました");
        }
    }
}