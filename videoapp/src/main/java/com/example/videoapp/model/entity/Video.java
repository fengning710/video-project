package com.example.videoapp.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

// 视频实体类（对应数据库字段，供数据库操作使用）
@Data
@Builder
public class Video {
    private Long id;
    private String vid;
    @NonNull
    private String title;

    private String description;
    private String filePath;
    private String coverPath;
    private Long userId;
    private LocalDateTime createdTime;

    @Builder.Default
    private Byte isDeleted = 0;
}
