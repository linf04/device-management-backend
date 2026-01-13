package com.device.management.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 認証例外
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnauthorizedException extends RuntimeException {
    private int code;
    private String message;

    public UnauthorizedException(String message) {
        super(message);
        this.code = 401;
        this.message = message;
    }
}
