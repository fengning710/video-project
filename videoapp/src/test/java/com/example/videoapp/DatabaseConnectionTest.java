package com.example.videoapp;

import com.example.videoapp.entity.User;
import com.example.videoapp.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DatabaseConnectionTest {

    // 自动注入UserMapper（Spring会通过MyBatis创建代理对象）
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testConnection() {
        // 假设数据库中已有id=1的用户
        User user = userMapper.findById(1l);
        System.out.println("查询到的用户：" + user);
        // 若控制台输出用户信息，说明连接成功；若为null，检查数据是否存在

        // 批量用id查询
        List<Long> ids = new ArrayList<>();
        ids.add(1l);ids.add(3l);ids.add(5l);
        List<User> users = userMapper.findByIds(ids);
        System.out.println("查询到的用户：");
        for (User user1 : users) {
            System.out.println(user1);
        }
    }
}
