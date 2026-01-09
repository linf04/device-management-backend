package com.device.management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(int code,String message) {
        super(message);
        this.code = code;
    }

}
