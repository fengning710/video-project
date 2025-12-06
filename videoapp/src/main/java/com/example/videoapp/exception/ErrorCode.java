package com.example.videoapp.exception;

// 业务码枚举
public enum ErrorCode {
    // 系统通用
    SUCCESS(200, "成功"), // 一般直接调Result类的success静态方法
    SYSTEM_ERROR(500, "系统异常，请稍后再试"),
    PARAM_ERROR(400, "参数错误"),

    // 用户相关(预留注册功能)
    USER_NOT_FOUND(401, "用户不存在"),
    PASSWORD_ERROR(402, "密码错误"),
    USER_NOT_LOGIN(403, "未登录，请先登录"),
    USER_HAD_FOUND(404,"用户已存在"),
    USER_WRONG_TOKEN(405,"token失效，请重新登录"),

    // 视频相关
    VIDEO_NOT_FOUND(411, "视频不存在"),
    VIDEO_NO_PERMISSION(412, "无权限访问该视频"),  // 预留后续添加VIP功能
    VIDEO_TOO_MAX(413, "视频大小超出上传限制"),
    VIDEO_INVALID_TYPE(414, "上传视频格式错误"),

    // 评论相关
    COMMENT_NOT_FOUND(421, "评论不存在"),
    COMMENT_EMPTY(422, "评论内容不能为空");

    private final int code;       // 状态码
    private final String message; // 错误信息

    // 枚举构造方法(默认private)，仅提供给自身的枚举常量使用
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
