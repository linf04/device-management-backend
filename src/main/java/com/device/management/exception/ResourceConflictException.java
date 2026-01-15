package com.device.management.exception;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源冲突异常（409）
 * 用于：设备ID重复、显示器被占用、IP地址被占用等场景
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceConflictException extends RuntimeException {
    private int code = 409;
    private String message;

    public ResourceConflictException(String message) {
        this.message = message;
    }
}
