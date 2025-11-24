package com.example.videoapp.controller.test;


import com.example.videoapp.model.entity.User;
import com.example.videoapp.mapper.UserMapper;
import com.example.videoapp.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/testcontroller")
    public Result<User> testController(@RequestParam Long testId) {
        System.out.println("testcontroller测试成功");
        User user = userMapper.findById(testId);
        if(user == null){
            return Result.fail(404,"用户信息不存在");
        }
        return Result.success(user);
    }

    @PostMapping("/testcontroller/testPost")
    public Result<User> testPost(@RequestBody User user){
        System.out.println("插入测试成功");
        try{
            int rows = userMapper.createUser(user);
            if(rows > 0){
                return Result.success(user,"用户创建成功");
            }
            return Result.fail(500,"用户创建失败");
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail(409,e.getMessage());
        }
    }

//    @PostMapping("/login")
//    public Result<User> login(@RequestBody User user){
//        System.out.println("login测试成功");
//        try{
//            User hadUser = userMapper.findByName(user.getUserName());
//            if(hadUser == null){
//                return Result.fail(404,"用户不存在");
//            }
//            if(hadUser.getUserPassword().equals(user.getUserPassword())) {
//                return Result.success(hadUser, "登录成功");
//            }
//            return Result.fail(401,"密码错误，用户登录失败");
//        }catch (Exception e){
//            e.printStackTrace();
//            return Result.fail(500,"服务器异常，请稍后再试(Java报)");
//        }
//    }
}
