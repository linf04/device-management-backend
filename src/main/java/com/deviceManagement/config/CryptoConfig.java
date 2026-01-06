package com.deviceManagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

    @Value("${encryption.aes.key}")
    private String aesKey;

    @Value("${encryption.aes.algorithm:AES/ECB/PKCS5Padding}")
    private String aesAlgorithm;

    public static String KEY;
    public static String ALG;

    public void init() {
        KEY = aesKey;
        ALG = aesAlgorithm;
    }
}
