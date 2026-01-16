package com.device.management.exception;

/**
 * リソース未検出例外（404）
 * リソース不存在時のシナリオ用
 *
 * @author device-management
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
