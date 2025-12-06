package com.example.videoapp.model.vo;

// 登录结果展示类（去除隐私信息，传输到前端供信息展示）
// 包装了用户展示类
// 因编写时间较早，未使用Lombok工具自动添加get/set方法和构造方法
public class LoginResultVO {
    private UserVO user;
    private String token;

    public LoginResultVO(UserVO user, String token) {
        this.user = user;
        this.token = token;
    }

    public LoginResultVO() {
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
