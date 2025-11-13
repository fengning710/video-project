package com.example.videoapp.controller;

import com.example.videoapp.entity.LoginResultVO;
import com.example.videoapp.entity.User;
import com.example.videoapp.entity.UserVO;
import com.example.videoapp.service.UserService;
import com.example.videoapp.util.JwtUtils;
import com.example.videoapp.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.videoapp.entity.UserVO.makeUserVO;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result loginControl(@RequestBody User user){
        // 1. 调用 Service（这里会自动捕获 BusinessException，由全局异常处理器处理）
        User userLogin = userService.loginService(user);

        // 2. 生成 token（Controller 层的工作）
        String token = JwtUtils.createToken(userLogin.getId());

        // 3. 包装成 LoginResultVO 和 Result 返回（Controller 层的工作）
        UserVO userVO = UserVO.makeUserVO(userLogin);
        LoginResultVO resultVO = new LoginResultVO();
        resultVO.setUser(userVO);
        resultVO.setToken(token);

        return Result.success(resultVO);
//        return userService.loginService(user);
    }

    @GetMapping("/findUser")
    public Result findUserByName(@RequestParam String name){
        User user = userService.findUserByName(name);
        return Result.success(user);
    }

    @GetMapping("/findUserById")
    public Result findUserById(@RequestParam Long id){
        UserVO userVO = makeUserVO(userService.findById(id));
        return Result.success(userVO);
    }

    @PostMapping("/register")
    public Result userRegister(@RequestBody User user){
        return Result.success(makeUserVO(userService.save(user)), "用户注册成功！");
    }

}
