package com.device.management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    // 异常码
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 默认业务异常码
    }
    public BusinessException(int code,String message) {
        super(message);
        this.code = code; // 默认业务异常码
    }

}
