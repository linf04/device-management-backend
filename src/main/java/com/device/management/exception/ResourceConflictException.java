package com.device.management.exception;

/**
 * リソース競合例外（409）
 * 使用例：デバイスID重複、モニター名使用中、IPアドレス使用中等のシナリオ
 *
 * @author device-management
 */
public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
