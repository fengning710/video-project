package com.example.videoapp.service;

import com.example.videoapp.model.entity.User;

// 用户业务新增接口
public interface UserService extends BaseService<User>{
    User loginService(User user);
    User findUserByName(String name);
    User findById(Long id);
}
