package com.device.management.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * リソース競合エラー（HTTP 400）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConflictException extends RuntimeException {
    private int code;
    private String message;

    public ConflictException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }
}