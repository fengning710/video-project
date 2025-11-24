package com.example.videoapp.model.vo;

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
