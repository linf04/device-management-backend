package com.device.management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * APIレスポンスラッパークラス
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long total;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer page;
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        ApiResponse<List<T>> response = new ApiResponse<>(200, "検索成功", data);
        response.setTotal(total);
        response.setPage(page);
        response.setSize(size);
        return response;
    }
}
