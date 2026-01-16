package com.device.management.exception;

/**
 * 一般例外（500）
 * 予期しないサーバー内部エラー用
 *
 * @author device-management
 */
public class AllException extends RuntimeException {

    public AllException(String message) {
        super(message);
    }

    public AllException(String message, Throwable cause) {
        super(message, cause);
    }
}
