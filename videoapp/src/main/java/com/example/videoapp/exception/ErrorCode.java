package com.example.videoapp.exception;

public enum ErrorCode {
    // 系统通用
    SUCCESS(200, "成功"),
    SYSTEM_ERROR(500, "系统异常，请稍后再试"),
    PARAM_ERROR(400, "参数错误"),

    // 用户相关(预留注册功能)
    USER_NOT_FOUND(401, "用户不存在"),
    PASSWORD_ERROR(402, "密码错误"),
    USER_NOT_LOGIN(403, "请先登录"),
    USER_HAD_FOUND(404,"用户已存在"),

    // 视频相关
    VIDEO_NOT_FOUND(411, "视频不存在"),
    VIDEO_NO_PERMISSION(412, "无权限访问该视频"),

    // 评论相关
    COMMENT_NOT_FOUND(421, "评论不存在"),
    COMMENT_EMPTY(422, "评论内容不能为空");

    private final int code;       // 状态码
    private final String message; // 错误信息

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // get方法（供外部获取code和message）
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
