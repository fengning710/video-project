package com.example.videoapp.model.dto;

import lombok.Data;

// 视频流传输类（视频播放使用 后端->前端）
@Data
public class VideoStreamInfo {
    private String filePath;
    private long contentLength;  // 本次传输总大小
    private long start;
    private long end;
    private long fileTotalLength;  // 文件总大小
    private String mimeType;
}
