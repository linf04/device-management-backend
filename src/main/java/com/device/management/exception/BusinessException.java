package com.device.management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*
* 業務異常
* */
public class BusinessException extends RuntimeException {
    // 异常码
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // デフォルトのビジネス例外コード
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

}
