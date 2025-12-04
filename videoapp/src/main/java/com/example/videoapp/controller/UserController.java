package com.example.videoapp.controller;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.model.vo.LoginResultVO;
import com.example.videoapp.model.entity.User;
import com.example.videoapp.model.vo.UserVO;
import com.example.videoapp.service.UserService;
import com.example.videoapp.util.JwtUtils;
import com.example.videoapp.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.example.videoapp.model.vo.UserVO.makeUserVO;

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

    // 供个人主页获取个人信息使用（解析token）
    @GetMapping("/user/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new BusinessException(ErrorCode.USER_WRONG_TOKEN);
        }
        User user = userService.findById(userId);
        return Result.success(UserVO.makeUserVO(user));
    }

}
