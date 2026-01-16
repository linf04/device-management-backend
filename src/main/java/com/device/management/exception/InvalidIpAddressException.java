package com.device.management.exception;

/**
 * IPアドレス形式無効例外（400）
 * IPアドレス形式検証失敗時のシナリオ用
 *
 * @author device-management
 */
public class InvalidIpAddressException extends RuntimeException {

    public InvalidIpAddressException(String message) {
        super(message);
    }

    public InvalidIpAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
