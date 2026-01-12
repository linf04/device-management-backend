package com.device.management.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 復号化例外クラス
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