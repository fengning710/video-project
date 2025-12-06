package com.example.videoapp.util;

import java.time.LocalDateTime;

// 核心：前端响应类（除了视频流响应）
// 目的：统一返回前端的响应信息规格，方便前端解析及获取信息并渲染
public class Result<T> {
    //提示信息，成功为“success”，失败为具体信息
    private String message;
    //状态码，成功200
    private Integer code;
    //数据
    private T data;
    //处理时间
    private LocalDateTime time;

    // 业务成功执行调用的静态工厂方法
    public static <T> Result<T> success(T getData){
        Result result = new Result();
        //直接设定业务码为 200
        result.setCode(200);
        result.setMessage("success");
        result.setData(getData);
        result.setTime(LocalDateTime.now());
        return result;
    }

    // 重载：可自定义成功信息描述
    public static <T> Result<T> success(T getData, String message){
        Result result = new Result();
        result.setCode(200);
        result.setMessage(message);
        result.setData(getData);
        result.setTime(LocalDateTime.now());
        return result;
    }

    // 核心：业务失败调用的静态工厂方法
    public static <T> Result<T> fail(Integer code, String message){
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        result.setTime(LocalDateTime.now());
        return result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
