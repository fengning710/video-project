package com.example.videoapp.model.vo;

import com.example.videoapp.model.entity.Video;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoVO {
    private Long id;
    private String vid;
    private String title;
    private String description;
    private String filePath;
    private String coverPath;
    private Long userId;
    private LocalDateTime createdTime;

    public VideoVO() {
    }
    // 静态方法转换
    public static VideoVO change(Video video){
        VideoVO videoVO = new VideoVO();
        videoVO.setId(video.getId());
        videoVO.setVid(video.getVid());
        videoVO.setTitle(video.getTitle());
        videoVO.setDescription(video.getDescription());
        videoVO.setFilePath(video.getFilePath());
        videoVO.setCoverPath(video.getCoverPath());
        videoVO.setUserId(video.getUserId());
        videoVO.setCreatedTime(video.getCreatedTime());
        return videoVO;
    }
}
