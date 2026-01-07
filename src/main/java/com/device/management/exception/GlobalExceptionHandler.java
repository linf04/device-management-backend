package com.device.management.exception;


import com.device.management.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/*import com.device.management.dto.ApiResponse;
import com.device.management.common.ApiResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;*/

/**
 * グローバル例外ハンドラー：全てのController層でスローされる例外を一括処理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
/*    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    *//**
     * カスタム業務例外（BusinessException）を処理
     * @param e 業務例外オブジェクト
     * @return 標準化されたResultレスポンス
     *//*
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        // 例外codeをResultCode列挙型にマッピング
        ApiResponseCode apiResponseCode = mapCodeToResultCode(e.getCode());
        logger.warn("業務エラー：code={}, message={}", apiResponseCode.getCode(), apiResponseCode.getMessage());
        // ジェネリック<Void>を指定してResult.errorメソッドの戻り値タイプに一致
        return ApiResponse.<Void>error(apiResponseCode);
    }

    *//**
     * パラメータ検証例外（@NotBlank、@Validなどによって発生する例外）を処理
     * @param e パラメータ検証例外オブジェクト
     * @return 標準化されたResultレスポンス
     *//*
    @ExceptionHandler(BindException.class)
    public ApiResponse<?> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
        logger.warn("パラメータ検証エラー：{}", errorMsg);
        // PARAM_ERROR列挙型のcodeを使用し、具体的なエラー情報を結合して返す
        return ApiResponse.<Void>error(ApiResponseCode.PARAM_ERROR, errorMsg);
    }

    *//**
     * その他の未キャッチなシステム例外を処理
     * @param e 例外オブジェクト
     * @return 標準化されたResultレスポンス
     *//*
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGlobalException(Exception e) {
        logger.error("システムエラー：", e); // 調査のための完全なスタックトレースを記録
        // FAIL列挙型を直接使用してシステムエラーを返す
        return ApiResponse.<Void>error(ApiResponseCode.FAIL);
    }

    *//**
     * 補助メソッド：業務例外codeをResultCode列挙型にマッピング
     * @param code 業務例外code
     * @return 一致するResultCode、デフォルトはシステムエラーを返す
     *//*
    private ApiResponseCode mapCodeToResultCode(int code) {
        for (ApiResponseCode codeEnum : ApiResponseCode.values()) {
            if (codeEnum.getCode() == code) {
                return codeEnum;
            }
        }
        return ApiResponseCode.FAIL;
    }*/


    /**
     * 处理资源不找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        log.error("Validation error: {}", message);
        ApiResponse<?> response = ApiResponse.error(400, "参数验证失败: " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理认证失败异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        log.error("Unauthorized: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.error(500, "服务器内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}