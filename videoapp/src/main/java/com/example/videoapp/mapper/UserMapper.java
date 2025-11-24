package com.example.videoapp.mapper;

import com.example.videoapp.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long id);
    User findByName(String userName);
    int createUser(User user);

    // 多个ID查询：返回所有ID匹配的用户列表
    // @Param("ids")：给参数命名，方便XML中引用（必须加，否则XML中无法识别集合）
    List<User> findByIds(@Param("ids") List<Long> ids);
}
