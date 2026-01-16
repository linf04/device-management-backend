package com.device.management.exception;

/**
 * パラメータ例外（400）
 * パラメータ検証失敗、パラメータ空、パラメータ形式エラー等のシナリオ用
 *
 * @author device-management
 */
public class ParameterException extends RuntimeException {

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
