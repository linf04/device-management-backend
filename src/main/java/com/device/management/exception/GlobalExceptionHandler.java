package com.device.management.exception;

import com.device.management.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * グローバル例外ハンドリング
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
        log.error("リソースが見つかりません: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * パラメーター検証例外を処理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        log.error("バリデーションエラー: {}", message);
        ApiResponse<?> response = ApiResponse.error(400, "パラメーター検証に失敗しました: " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 認証失敗例外を処理
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        log.error("認証失敗: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    /**
    * 业务异常
    * */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        log.error("Business error: {}", ex.getMessage());
        ApiResponse<String> response = ApiResponse.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    /**
     * その他のすべての例外を処理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.error(500, "服务器内部错误："+ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}