package com.device.management.exception;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * IP アドレスフォーマットが無効な異常
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)  // 这确保返回400状态码
public class InvalidIpAddressException extends RuntimeException {
    private int code;
    private String message;

    public InvalidIpAddressException(String message) {
        super(message);
        this.code = 400;  // 业务代码也设置为400
        this.message = message;
    }
}

