package com.example.videoapp.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

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
