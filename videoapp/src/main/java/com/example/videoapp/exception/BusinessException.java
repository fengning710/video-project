package com.example.videoapp.exception;

// 统一异常类(继承运行中异常不会中断项目运行)
public class BusinessException extends RuntimeException {
    // 枚举存业务错误码
    private final ErrorCode errorCode; // 携带错误码

    // 构造方法：传入错误码
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 调用父类的构造方法，传入枚举内定义的错误信息
        this.errorCode = errorCode;
    }

    // 构造方法重载：支持自定义错误信息（比如"视频ID=100不存在"）
    public BusinessException(ErrorCode errorCode, String message) {
        super(message); // 传入其他抛异常的自定义错误信息
        this.errorCode = errorCode;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
