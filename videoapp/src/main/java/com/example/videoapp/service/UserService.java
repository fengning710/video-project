package com.example.videoapp.service;

import com.example.videoapp.entity.User;
import com.example.videoapp.util.Result;

public interface UserService extends BaseService{
    Result loginService(User user);
    Result findUserByName(String name);
    Result findById(Long id);
}
