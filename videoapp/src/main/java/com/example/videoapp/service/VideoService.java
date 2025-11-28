package com.example.videoapp.service;

import com.example.videoapp.model.dto.VideoStreamInfo;
import com.example.videoapp.model.entity.Video;
import com.example.videoapp.model.vo.PageResult;
import com.example.videoapp.model.vo.VideoVO;

import java.util.List;

public interface VideoService extends BaseService{

    // --- 新增：获取视频流 ---
    VideoStreamInfo getVideoStream(String filename, String rangeHeader);

    Video getVideo(Long id);
    Video getVideo(String vid);

    // 新增：视频模糊(关键字)查询
    PageResult<VideoVO> getVideoPageList(Long pageNum, Integer pageSize, String keyword);
}
