package com.example.videoapp.controller;

import com.example.videoapp.entity.User;
import com.example.videoapp.service.UserService;
import com.example.videoapp.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result loginControl(@RequestBody User user){
        return userService.loginService(user);
    }

    @GetMapping("/findUser")
    public Result findUserByName(@RequestParam String name){
        return userService.findUserByName(name);
    }

    @GetMapping("/findUserById")
    public Result findUserById(@RequestParam Long id){
        return userService.findById(id);
    }

    @PostMapping("/register")
    public Result userRegister(@RequestBody User user){
        return userService.save(user);
    }

}
