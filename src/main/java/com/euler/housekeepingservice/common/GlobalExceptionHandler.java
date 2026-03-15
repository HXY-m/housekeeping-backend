package com.euler.housekeepingservice.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常统一拦截器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截自定义业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常拦截: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 拦截 Jakarta Validation 参数校验异常 (配合 @Valid / @Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("参数校验失败: ");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getField()).append(" ")
                    .append(fieldError.getDefaultMessage()).append("; ");
        }
        log.warn("参数校验异常: {}", errorMessage.toString());
        return Result.error(400, errorMessage.toString());
    }

    /**
     * 兜底拦截不可预知的系统级异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统内部未捕获异常: ", e);
        return Result.error(500, "系统繁忙，请稍后再试");
    }
}