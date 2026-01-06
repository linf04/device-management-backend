package com.deviceManagement.dto;

import com.deviceManagement.common.ApiResponseCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


/**
 * 统一API响应结果类（与ResultCode枚举绑定，完全匹配API规范）
 * @param <T> 响应数据类型（登录成功时为LoginData，登出/错误时为Void）
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 通用成功响应
     * @param data 响应数据
     * @return Result<T>
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            ApiResponseCode.SUCCESS.getCode(),
            ApiResponseCode.SUCCESS.getMessage(),
            data
        );
    }


    /**
     * ログイン成功レスポンス（APIのLoginResponseに一致）
     * @param loginData ログイン成功データ（token+userInfoを含む）
     * @return Result<LoginData>
     */
    public static ApiResponse<LoginResponse> loginSuccess(LoginResponse loginData) {
        return new ApiResponse<>(
                ApiResponseCode.LOGIN_SUCCESS.getCode(),
                ApiResponseCode.LOGIN_SUCCESS.getMessage(),
                loginData
        );
    }

    /**
     * ログアウト成功レスポンス（APIのLogoutResponseに一致）
     * @return Result<Void>
     */
    public static ApiResponse<Void> logoutSuccess() {
        return new ApiResponse<>(
                ApiResponseCode.LOGOUT_SUCCESS.getCode(),
                ApiResponseCode.LOGOUT_SUCCESS.getMessage(),
                null
        );
    }

    /**
     * エラーレスポンス（ジェネリック対応、任意の戻り値タイプに適合）
     * @param apiResponseCode エラー列挙型
     * @return Result<T>：指定されたジェネリックのエラー結果
     */
    public static <T> ApiResponse<T> error(ApiResponseCode apiResponseCode) {
        return new ApiResponse<>(
                apiResponseCode.getCode(),
                apiResponseCode.getMessage(),
                null
        );
    }
    //オーバーロードメソッド
    public static <T> ApiResponse<T> error(ApiResponseCode apiResponseCode, String customMessage) {
        return new ApiResponse<>(apiResponseCode.getCode(), customMessage, null);
    }

    /**
     * パスワード変更成功レスポンス
     * @param data
     * @return
     */
    public static ApiResponse<ChangePasswordResponse> passwordChangedSuccess(ChangePasswordResponse data) {
        return new ApiResponse<>(
                ApiResponseCode.PASSWORD_CHANGED_SUCCESS.getCode(),
                ApiResponseCode.PASSWORD_CHANGED_SUCCESS.getMessage(),
                null
        );
    }
}