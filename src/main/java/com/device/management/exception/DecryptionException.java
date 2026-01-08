package com.device.management.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解密异常类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecryptionException extends RuntimeException {
    private int code;
    private String message;

    public DecryptionException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }
}