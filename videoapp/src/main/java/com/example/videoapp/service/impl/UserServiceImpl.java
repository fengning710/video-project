package com.example.videoapp.service.impl;

import com.example.videoapp.model.entity.User;
import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.service.UserService;
import com.example.videoapp.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User loginService(User user) {
        System.out.println("userServiceImpl测试成功");

        User hadUser = userMapper.findByName(user.getUserName());
        if(hadUser == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if(!hadUser.getUserPassword().equals(user.getUserPassword())){
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        return hadUser;
        //return Result.success(makeUserVO(hadUser),"登录成功");
    }

    @Override
    public User findUserByName(String name) {
        if(name == null || name.isEmpty()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名不能为空！");
        }
        User user = userMapper.findByName(name);
        if(user == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
        //return Result.success(makeUserVO(user));
    }

    @Override
    public User findById(Long id) {
        if(id == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"id不能为空！");
        }else if(id < 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "id不能为复数！");
        }
        User user = userMapper.findById(id);
        if(user == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
        //return Result.success(makeUserVO(user));
    }

    @Override
    public User save(User user) {
        //获取对象的判断
        if(user == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户参数为空");
        }

        // 判断内容
        if(user.getUserName() == null || user.getUserName().isEmpty()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名不能为空！");
        }
        if(user.getUserPassword() == null || user.getUserPassword().isEmpty()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码不能为空！");
        }
        // 数据库找用户，判断是否存在
        User hadUser = userMapper.findByName(user.getUserName());
        if(hadUser != null){
            throw new BusinessException(ErrorCode.USER_HAD_FOUND);
        }
        // 创建，判断修改行数是否大于0
        if(userMapper.createUser(user) <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统出错，注册失败，请重新再试");
        }
        return userMapper.findByName(user.getUserName());
        //return Result.success(makeUserVO(userMapper.findByName(user.getUserName())), "用户注册成功！");
    }

    @Override
    public User deleteById(Long id) {
        return null;
    }

    @Override
    public boolean update(User user) {
        return false;
    }
}
