package com.example.videoapp.model.vo;

import com.example.videoapp.model.entity.Video;
import lombok.Data;

import java.time.LocalDateTime;

// 视频展示类（去除隐私信息，传输到前端供信息展示）
@Data
public class VideoVO {
    private Long id;
    private String vid;
    private String title;
    private String description;
    private String filePath;
    private String coverPath; // 封面路径（后续扩展展示视频封面使用）
    private LocalDateTime createdTime;
    private String author;

    public VideoVO() {
    }
    // 静态方法转换（将 Video 实体类+用户名 转换为展示类）
    public static VideoVO makeVideoVO(Video video, String author) {
        VideoVO videoVO = new VideoVO();
        videoVO.setId(video.getId());
        videoVO.setVid(video.getVid());
        videoVO.setTitle(video.getTitle());
        videoVO.setDescription(video.getDescription());
        videoVO.setFilePath(video.getFilePath());
        videoVO.setCoverPath(video.getCoverPath());
        videoVO.setCreatedTime(video.getCreatedTime());
        videoVO.setAuthor(author);
        return videoVO;
    }
}
