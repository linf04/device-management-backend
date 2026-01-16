package com.device.management.exception;

import com.device.management.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * グローバル例外ハンドラー：すべてのController層でスローされる例外を統一的に処理
 *
 * @author device-management
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * リソース未検出例外処理（404）
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * パラメータ検証例外処理（400）
     */
    @ExceptionHandler(ParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleParameterException(
            ParameterException ex, WebRequest request) {
        log.warn("Parameter validation error: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(400, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * IPアドレス形式無効例外処理（400）
     */
    @ExceptionHandler(InvalidIpAddressException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidIpAddressException(
            InvalidIpAddressException ex, WebRequest request) {
        log.warn("Invalid IP address format: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(400, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * リソース競合例外処理（409）
     * 使用例：デバイスID重複、モニター名使用中、IPアドレス使用中など
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceConflictException(
            ResourceConflictException ex, WebRequest request) {
        log.warn("Resource conflict: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(409, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Springパラメータ検証例外処理（400）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.warn("Validation error: {}", message);
        ApiResponse<?> response = ApiResponse.error(400, "参数校验失败: " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 認証失敗例外処理（401）
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 復号化例外処理（400）
     */
    @ExceptionHandler(DecryptionException.class)
    public ResponseEntity<ApiResponse<?>> handleDecryptionException(
            DecryptionException ex, WebRequest request) {
        log.warn("Decryption failed: {}", ex.getMessage());
        int code = ex.getCode() != 0 ? ex.getCode() : 400;
        String message = ex.getMessage() != null ? ex.getMessage() : "密码格式无效";
        ApiResponse<?> response = ApiResponse.error(code, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 一般例外処理（500）
     */
    @ExceptionHandler(AllException.class)
    public ResponseEntity<ApiResponse<?>> handleAllException(
            AllException ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "服务器内部错误";
        ApiResponse<?> response = ApiResponse.error(500, message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * ビジネスロジック例外処理（400）
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        log.warn("Business logic error: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(400, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 不正引数例外処理（400）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.warn("Invalid argument: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(400, "无效参数: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * その他のキャッチされなかった例外処理（500）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(
            Exception ex, WebRequest request) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.error(500, "服务器内部错误: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
