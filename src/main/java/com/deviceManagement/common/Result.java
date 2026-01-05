package com.deviceManagement.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.deviceManagement.dto.LoginResponse;
import lombok.Data;


/**
 * 统一API响应结果类（与ResultCode枚举绑定，完全匹配API规范）
 * @param <T> 响应数据类型（登录成功时为LoginData，登出/错误时为Void）
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private int code;       // HTTP状态码（直接取自ResultCode）
    private String message; // 响应消息（直接取自ResultCode）
    private T data;         // 响应数据（仅成功带数据时存在）

    // 私有构造：强制通过静态工厂方法创建，避免硬编码code/message
    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 登录成功响应（匹配API的LoginResponse）
     * @param loginData 登录成功数据（含token+userInfo）
     * @return Result<LoginData>
     */
    public static Result<LoginResponse> loginSuccess(LoginResponse loginData) {
        return new Result<>(
                ResultCode.LOGIN_SUCCESS.getCode(),
                ResultCode.LOGIN_SUCCESS.getMessage(),
                loginData
        );
    }

    /**
     * 登出成功响应（匹配API的LogoutResponse）
     * @return Result<Void>
     */
    public static Result<Void> logoutSuccess() {
        return new Result<>(
                ResultCode.LOGOUT_SUCCESS.getCode(),
                ResultCode.LOGOUT_SUCCESS.getMessage(),
                null
        );
    }

    /**
     * 错误响应（支持泛型，适配任意返回类型）
     * @param resultCode 错误枚举
     * @return Result<T>：指定泛型的错误结果
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(
                resultCode.getCode(),
                resultCode.getMessage(),
                null
        );
    }
    //重载方法
    public static <T> Result<T> error(ResultCode resultCode, String customMessage) {
        return new Result<>(resultCode.getCode(), customMessage, null);
    }
}