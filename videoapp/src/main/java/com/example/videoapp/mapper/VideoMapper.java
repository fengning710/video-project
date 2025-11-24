package com.example.videoapp.mapper;

import com.example.videoapp.model.entity.Video;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoMapper {
    Video findVideoById(Long id);
    Video findVideoByVid(String vid);
}
