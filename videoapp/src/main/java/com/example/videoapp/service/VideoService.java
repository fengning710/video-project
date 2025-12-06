package com.example.videoapp.service;

import com.example.videoapp.model.dto.VideoStreamInfo;
import com.example.videoapp.model.dto.VideoUploadDTO;
import com.example.videoapp.model.entity.Video;
import com.example.videoapp.model.vo.PageResult;
import com.example.videoapp.model.vo.VideoVO;

// 视频业务新增接口
public interface VideoService extends BaseService{

    // 获取视频流
    VideoStreamInfo getVideoStream(String filename, String rangeHeader);

    // 获取视频信息
    VideoVO getVideoInfo(String videoId);


    // 视频模糊(关键字)查询
    PageResult<VideoVO> getVideoPageList(Long pageNum, Integer pageSize, String keyword, Long userId);

    // 视频上传方法(无封面，1GB限制，全量(非分片)上传)
    Video uploadVideo(VideoUploadDTO uploadDTO);

    // vid创建方法
    String createVid();

    // 检验vid是否存在
    boolean checkVidExists(String vid);

}