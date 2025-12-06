package com.example.videoapp.model.vo;

import lombok.Data;

import java.util.List;

// 视频列表展示类（包装视频信息列表和列表信息）
// 避免使用Map结构
@Data
public class PageResult<T> {
    private List<T> data;  // 列表数据
    private Long total; // 总条数
    private Long totalPage; // 总页数
    private Long pageNum; // 当前页数
    private Integer pageSize; // 每页条数

    public PageResult() {
    }

    public PageResult(List<T> data, Long total, Long totalPage, Long pageNum, Integer pageSize) {
        this.data = data;
        this.total = total;
        this.totalPage = totalPage;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
