package com.example.videoapp.service;

import com.example.videoapp.model.entity.User;

public interface UserService extends BaseService<User>{
    User loginService(User user);
    User findUserByName(String name);
    User findById(Long id);
}
