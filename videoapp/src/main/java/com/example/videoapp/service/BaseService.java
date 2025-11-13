package com.example.videoapp.service;

import com.example.videoapp.util.Result;

// 通用业务逻辑接口
public interface BaseService<T> {
    // 根据id查询
    T findById(Long id);
    // 新增
    T save(T t);
    // 删除
    T deleteById(Long id);
    // 修改
    boolean update(T t);
}
