package com.example.videoapp.service.impl;

import com.example.videoapp.model.entity.User;
import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.service.UserService;
import com.example.videoapp.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 用户业务实现类
@Service
public class UserServiceImpl implements UserService {
    // 字段注入
    @Autowired
    private UserMapper userMapper;

    // 登录业务实现（核心）
    @Override
    public User loginService(User user) {
        // 数据库查对应用户名（唯一）的用户
        User hadUser = userMapper.findByName(user.getUserName());
        if(hadUser == null){ // 数据库查不到
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 验证密码
        if(!hadUser.getUserPassword().equals(user.getUserPassword())){
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        // 返回用户信息供前端渲染
        return hadUser;
    }

    // 查用户（实际未用到）
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
    }

    // 通过id查用户实现方法
    @Override
    public User findById(Long id) {
        // 初步判断id
        if(id == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"id不能为空！");
        }else if(id < 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "id不能为复数！");
        }
        // 数据库查询
        User user = userMapper.findById(id);
        if(user == null){ // 查不到则抛异常
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    // 注册业务实现
    @Override
    public User save(User user) {
        //对传入用户对象的判断
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
        if(hadUser != null){ // 查到则抛异常（因为是注册逻辑）
            throw new BusinessException(ErrorCode.USER_HAD_FOUND);
        }
        // 创建，判断修改行数是否大于0
        if(userMapper.createUser(user) <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统出错，注册失败，请重新再试");
        }
        return userMapper.findByName(user.getUserName());
    }

    // 暂时未实现
    @Override
    public User deleteById(Long id) {
        return null;
    }

    @Override
    public boolean update(User user) {
        return false;
    }
}