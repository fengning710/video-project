package com.example.videoapp.model.dto;

import lombok.Data;

@Data
public class VideoStreamInfo {
    private String filePath;
    private long contentLength;  // 本次传输总大小
    private long start;
    private long end;
    private long fileTotalLength;  // 文件总大小
    private String mimeType;
}
