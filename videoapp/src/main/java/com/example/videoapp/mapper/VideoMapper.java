package com.example.videoapp.mapper;

import com.example.videoapp.model.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper {
    Video findVideoById(@Param("id") Long id);
    Video findVideoByVid(@Param("vid") String vid);
    //按关键字查视频（返回列表）
    List<Video> findVideoList(@Param("keyword") String keyword);
    //返回对应视频的个数（配合关键字查视频使用）
    Long findVideoTotal(@Param("keyword") String keyword);

    // 添加视频信息到数据库（创建视频）
    int insertVideo(Video video);
}
