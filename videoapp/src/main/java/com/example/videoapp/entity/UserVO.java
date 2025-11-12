package com.example.videoapp.entity;

import java.time.LocalDateTime;

public class UserVO {
    private Long id;
    private String userName;
    private LocalDateTime createTime;

    public UserVO() {
    }
    public UserVO(Long id, String userName, LocalDateTime createTime) {
        this.id = id;
        this.userName = userName;
        this.createTime = createTime;
    }

    public static UserVO makeUserVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUserName(user.getUserName());
        userVO.setCreateTime(user.getCreateTime());
        return userVO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
