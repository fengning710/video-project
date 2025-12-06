package com.example.videoapp.mapper;

import com.example.videoapp.model.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 对接数据库操作的视频信息处理接口
@Mapper
public interface VideoMapper {
    // 查询视频接口(区分id（Long类型）和vid（String类型，后端自定义生成）)
    Video findVideoById(@Param("id") Long id);
    Video findVideoByVid(@Param("vid") String vid);
    // 关键词列表查询视频接口
    List<Video> findVideoList(@Param("keyword") String keyword, @Param("userId") Long userId);

    //返回对应视频的个数（配合关键字查视频使用）
    Long findVideoTotal(@Param("keyword") String keyword, @Param("userId") Long userId);

    // 添加视频信息到数据库（创建视频）
    int insertVideo(Video video);
}
