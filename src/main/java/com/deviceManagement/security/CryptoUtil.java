package com.deviceManagement.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class CryptoUtil {

    private static String KEY;
    private static String ALG;

    @Value("${encryption.aes.key}")
    private String configKey;

    @Value("${encryption.aes.algorithm:AES/ECB/PKCS5Padding}")
    private String configAlg;

    public void init() {
        KEY = configKey;
        ALG = configAlg;

        if (KEY == null || KEY.length() != 16) {
            throw new IllegalArgumentException("AESキーは16桁である必要があります");
        }
    }

    public static String encrypt(String raw) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("暗号化に失敗しました", e);
            throw new RuntimeException("暗号化に失敗しました", e);
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
        } catch (Exception e) {
            log.error("復号に失敗しました: {}", base64, e);
            throw new RuntimeException("復号に失敗しました", e);
        }
    }
}