package com.deviceManagement.exception;

import com.deviceManagement.common.Result;
import com.deviceManagement.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;

/**
 * 全局异常处理器：统一处理所有Controller层抛出的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常（BusinessException）
     * @param e 业务异常对象
     * @return 标准化Result响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        // 根据异常code映射到ResultCode枚举
        ResultCode resultCode = mapCodeToResultCode(e.getCode());
        logger.warn("業務エラー：code={}, message={}", resultCode.getCode(), resultCode.getMessage());
        // 指定泛型<Void>匹配Result.error方法的返回类型
        return Result.<Void>error(resultCode);
    }

    /**
     * 处理参数校验异常（如@NotBlank、@Valid触发的异常）
     * @param e 参数校验异常对象
     * @return 标准化Result响应
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
        logger.warn("パラメータチェックエラー：{}", errorMsg);
        // 使用PARAM_ERROR枚举的code，结合具体错误信息返回
        return Result.<Void>error(ResultCode.PARAM_ERROR, errorMsg);
    }

    /**
     * 处理其他未捕获的系统异常
     * @param e 异常对象
     * @return 标准化Result响应
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleGlobalException(Exception e) {
        logger.error("システムエラー：", e); // 记录完整堆栈用于排查
        // 直接使用FAIL枚举返回系统错误
        return Result.<Void>error(ResultCode.FAIL);
    }

    /**
     * 辅助方法：将业务异常code映射到ResultCode枚举
     * @param code 业务异常code
     * @return 匹配的ResultCode，默认返回系统错误
     */
    private ResultCode mapCodeToResultCode(int code) {
        for (ResultCode codeEnum : ResultCode.values()) {
            if (codeEnum.getCode() == code) {
                return codeEnum;
            }
        }
        return ResultCode.FAIL;
    }
}