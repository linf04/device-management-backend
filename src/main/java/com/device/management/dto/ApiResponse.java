package com.device.management.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/*
import com.device.management.common.ApiResponseCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
*/


/**
 * 統一APIレスポンス結果クラス（ResultCode列挙型にバインド、API仕様に完全一致）
 * @param <T> レスポンスデータタイプ（ログイン成功時はLoginData、ログアウト/エラー時はVoid）
 */
/*
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

    */
/**
     * 共通成功レスポンス
     * @param data レスポンスデータ
     * @return Result<T>
     *//*

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            ApiResponseCode.SUCCESS.getCode(),
            ApiResponseCode.SUCCESS.getMessage(),
            data
        );
    }


    */
/**
     * ログイン成功レスポンス（APIのLoginResponseに一致）
     * @param loginData ログイン成功データ（token+userInfoを含む）
     * @return Result<LoginData>
     *//*

    public static ApiResponse<LoginResponse> loginSuccess(LoginResponse loginData) {
        return new ApiResponse<>(
                ApiResponseCode.LOGIN_SUCCESS.getCode(),
                ApiResponseCode.LOGIN_SUCCESS.getMessage(),
                loginData
        );
    }

    */
/**
     * ログアウト成功レスポンス（APIのLogoutResponseに一致）
     * @return Result<Void>
     *//*

    public static ApiResponse<Void> logoutSuccess() {
        return new ApiResponse<>(
                ApiResponseCode.LOGOUT_SUCCESS.getCode(),
                ApiResponseCode.LOGOUT_SUCCESS.getMessage(),
                null
        );
    }

    */
/**
     * エラーレスポンス（ジェネリック対応、任意の戻り値タイプに適合）
     * @param apiResponseCode エラー列挙型
     * @return Result<T>：指定されたジェネリックのエラー結果
     *//*

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

    */
/**
     * パスワード変更成功レスポンス
     * @param data
     * @return
     *//*

    public static ApiResponse<ChangePasswordResponse> passwordChangedSuccess(ChangePasswordResponse data) {
        return new ApiResponse<>(
                ApiResponseCode.PASSWORD_CHANGED_SUCCESS.getCode(),
                ApiResponseCode.PASSWORD_CHANGED_SUCCESS.getMessage(),
                null
        );
    }
}*/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private Long total;
    private Integer page;
    private Integer size;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "成功", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message);
    }

    public static <T> ApiResponse<List<T>> page(List<T> data, Long total, Integer page, Integer size) {
        ApiResponse<List<T>> response = new ApiResponse<>(200, "查询成功", data);
        response.setTotal(total);
        response.setPage(page);
        response.setSize(size);
        return response;
    }
}