package com.device.management.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
    private int code;
    private String message;

    public ResourceNotFoundException(String message) {
        super(message);
        this.code = 404;
        this.message = message;
    }
}
