package com.example.videoapp.config;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 全局异常处理器：捕获所有Controller层抛出的异常
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 捕获自定义的业务异常（优先级最高）
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        // 返回错误码+错误信息
        return Result.fail(errorCode.getCode(), e.getMessage());
    }

    // 2. 捕获参数错误异常（比如前端传参格式不对）
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        // 用ErrorCode.PARAM_ERROR的状态码，错误信息用具体异常信息
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    // 3. 捕获系统其他异常（兜底，防止漏网之鱼）
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 生产环境可以打印日志，方便排查；开发环境可以返回具体错误信息
        e.printStackTrace(); // 开发时打印堆栈，方便调试
        return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), "系统异常：" + e.getMessage());
    }
}
