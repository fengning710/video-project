package com.example.videoapp.service;

import com.example.videoapp.entity.User;
import com.example.videoapp.util.Result;

public interface UserService extends BaseService<User>{
    User loginService(User user);
    User findUserByName(String name);
    User findById(Long id);
}
