package com.example.videoapp.exception;

public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode; // 携带错误码

    // 构造方法：传入错误码
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 调用父类的构造方法，传入错误信息
        this.errorCode = errorCode;
    }

    // 也可以自定义错误信息（比如"视频ID=100不存在"）
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
