package com.example.videoapp.controller.test;


import com.example.videoapp.testDemo.mySQL.UserTest;
import org.springframework.web.bind.annotation.*;

@RestController
public class LearnController {
    @GetMapping("/learn/test")
    //下面注解，前端发送请求时会将信息拼接到url中
    public String test(@RequestParam String testParam){
        System.out.println("测试成功");
        if(testParam.contains("流萤")){
            return "小馋猫";
        }
        return testParam;
    }

    @GetMapping("/learn/testFront")
    public String testFront(@RequestParam String testParam) {
        // 后端处理逻辑，这里简化返回一个字符串
        return "这是后端返回的用户数据：[{name: '张三'}, {name: '李四'}]";
    }

    //注意下方的注解（下面那个），前端必须发送json表单
    @PostMapping("/learn/testPost")
    public String testPost(@RequestBody UserTest userTest){
        return "成功收到信息:"+userTest.toString();
    }

    @PutMapping("/users/{userId}")
    public String testPut(@PathVariable Integer userId, @RequestBody UserTest userTest){
        return "更新用户信息为：" +  userTest;
    }

    @DeleteMapping("/users/de/{userId}")
    public String testDelete(@PathVariable Integer userId){
        return "已经删除用户信息，id:"+userId;
    }
}
