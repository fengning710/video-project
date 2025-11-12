package com.example.videoapp.service;

import com.example.videoapp.util.Result;

// 通用业务逻辑接口
public interface BaseService<T> {
    // 根据id查询
    Result findById(Long id);
    // 新增
    Result save(T t);
    // 删除
    Result deleteById(Long id);
    // 修改
    Result update(T t);
}
