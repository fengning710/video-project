package com.example.videoapp.service;

import com.example.videoapp.model.dto.VideoStreamInfo;
import com.example.videoapp.model.entity.Video;

public interface VideoService extends BaseService{

    // --- 新增：获取视频流 ---
    VideoStreamInfo getVideoStream(String filename, String rangeHeader);

    Video getVideo(Long id);
    Video getVideo(String vid);
}
