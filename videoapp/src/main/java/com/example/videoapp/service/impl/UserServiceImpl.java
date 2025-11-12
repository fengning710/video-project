package com.example.videoapp.service.impl;

import com.example.videoapp.entity.User;
import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.service.UserService;
import com.example.videoapp.mapper.UserMapper;
import com.example.videoapp.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.videoapp.entity.UserVO.makeUserVO;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result loginService(User user) {
        System.out.println("userServiceImpl测试成功");

        User hadUser = userMapper.findByName(user.getUserName());
        if(hadUser == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if(!hadUser.getUserPassword().equals(user.getUserPassword())){
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        return Result.success(makeUserVO(hadUser),"登录成功");
    }

    @Override
    public Result findUserByName(String name) {
        if(name == null || name.isEmpty()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名不能为空！");
        }
        User user = userMapper.findByName(name);
        if(user == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return Result.success(makeUserVO(user));
    }

    @Override
    public Result findById(Long id) {
        if(id == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"id不能为空！");
        }else if(id < 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "id不能为复数！");
        }
        User user = userMapper.findById(id);
        if(user == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return Result.success(makeUserVO(user));
    }

    @Override
    public Result save(Object o) {
        //获取对象的判断
        if(o == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户参数为空");
        }
        if(!(o instanceof User)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户类型转换错误");
        }
        // 转型+判断内容
        User user = (User)o;
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
        return Result.success(makeUserVO(userMapper.findByName(user.getUserName())), "用户注册成功！");
    }

    @Override
    public Result deleteById(Long id) {
        return null;
    }

    @Override
    public Result update(Object o) {
        return null;
    }
}
