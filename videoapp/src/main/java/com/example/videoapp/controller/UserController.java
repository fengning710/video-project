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

// 用户相关和前端对接的接口类
@RestController
public class UserController {
    // 字段注入
    @Autowired
    private UserService userService;

    // 登录接口
    @PostMapping("/login")
    public Result loginControl(@RequestBody User user){
        // 调用 Service（会自动捕获 BusinessException，由全局异常处理器处理）
        User userLogin = userService.loginService(user);

        // 生成 token（调用 JwtUtils 的静态方法生成）
        String token = JwtUtils.createToken(userLogin.getId());

        // 包装成 LoginResultVO 并打入 Result 返回
        UserVO userVO = UserVO.makeUserVO(userLogin);
        LoginResultVO resultVO = new LoginResultVO(userVO, token);
        return Result.success(resultVO);
    }

    // 根据用户名查找用户接口（项目未实际用到） 后续可扩展为通过搜索栏查询用户
    @GetMapping("/findUser")
    public Result findUserByName(@RequestParam String name){
        User user = userService.findUserByName(name);
        return Result.success(user);
    }

    // 根据用户id查找用户接口（项目未实际用到）
    @GetMapping("/findUserById")
    public Result findUserById(@RequestParam Long id){
        UserVO userVO = makeUserVO(userService.findById(id));
        return Result.success(userVO);
    }

    // 注册接口
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
