package com.device.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)   // 让 Spring 返回 400
public class ParameterException  extends RuntimeException {
    public ParameterException (String msg) {
        super(msg);
    }
}