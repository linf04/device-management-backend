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
 * グローバル例外ハンドラー：全てのController層でスローされる例外を一括処理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * リソースが見つからない例外を処理
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * パラメータ検証例外を処理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        log.error("Validation error: {}", message);
        ApiResponse<?> response = ApiResponse.error(400, "パラメータ検証に失敗しました: " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 認証失敗例外を処理
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        log.error("Unauthorized: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 復号化例外を処理
     */
    @ExceptionHandler(DecryptionException.class)
    public ResponseEntity<ApiResponse<?>> handleDecryptionException(
            DecryptionException ex, WebRequest request) {
        log.warn("復号に失敗しました: {}", ex.getMessage());
        int code = ex.getCode() != 0 ? ex.getCode() : 400;
        String message = ex.getMessage() != null ? ex.getMessage() : "パスワードフォーマットが無効です";

        ApiResponse<?> response = ApiResponse.error(code, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * その他の全ての例外を処理
     */
    @ExceptionHandler(AllException.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.error(500, "サーバー内部エラー");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}