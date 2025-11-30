package com.example.videoapp.model.dto;


import lombok.Data;

import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoUploadDTO {
    private MultipartFile file; // 视频文件流
    private Long userId; // 上传用户id
    private String title; // 视频标题
}
