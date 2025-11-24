package com.example.videoapp.service.impl;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.mapper.VideoMapper;
import com.example.videoapp.model.dto.VideoStreamInfo;
import com.example.videoapp.model.entity.Video;
import com.example.videoapp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Value("${videoArea.baseUrl}")
    private String videoBasePath;

    @Override
    public VideoStreamInfo getVideoStream(String filename, String rangeHeader) throws BusinessException{

        // 验证文件名，防止路径穿越攻击 (非常重要！)
        String fileName = StringUtils.cleanPath(filename);
        fileName = videoMapper.findVideoByVid(fileName).getFilePath();
        Path videoPath = Paths.get(videoBasePath).resolve(fileName);
        File file = videoPath.toFile();

        // 判断文件是否存在
        if (!file.exists() || !file.isFile()) {
            throw new BusinessException(ErrorCode.VIDEO_NOT_FOUND);
        }

        long fileLength = file.length();  //视频长度
        VideoStreamInfo info = new VideoStreamInfo();
        info.setFilePath(file.toString());
        info.setFileTotalLength(fileLength);
        info.setMimeType(getContentType(file.toString())); //获取内容类型

        // 4. 解析Range请求头，计算startByte和endByte
        if (StringUtils.hasText(rangeHeader)) {
            // 正则匹配Range格式（支持：bytes=0-1023、bytes=1024-、bytes=-512）
            Matcher matcher = Pattern.compile("bytes=(\\d+)-(\\d+)?").matcher(rangeHeader);
            if (!matcher.find()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Range请求格式错误（示例：Range: bytes=0-1023）");
            }

            // 计算起始字节（startByte）
            long startByte = Long.parseLong(matcher.group(1));
            if (startByte < 0 || startByte >= fileLength) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Range起始字节超出范围（总长度：" + fileLength + "）");
            }

            // 计算结束字节（endByte）
            String endGroup = matcher.group(2);
            long endByte = StringUtils.hasText(endGroup) ? Long.parseLong(endGroup) : fileLength - 1;
            // 修正endByte：不能超过总长度-1，不能小于startByte
            endByte = Math.min(endByte, fileLength - 1);
            endByte = Math.max(endByte, startByte);

            // 4.4 赋值给info
            info.setStart(startByte);
            info.setEnd(endByte);
            info.setContentLength(endByte - startByte + 1); // 本次传输的字节数
        } else {
            // 如果没有Range请求头（全量传输）
            info.setStart(0);
            info.setEnd(fileLength - 1);
            info.setContentLength(fileLength);
        }
        return info;
    }

    private String getContentType(String filePath) {
        try {
            // 用Java NIO的Files工具类自动探测文件类型（推荐，支持多种格式）
            return Files.probeContentType(new File(filePath).toPath());
        } catch (IOException e) {
            // 异常时根据后缀名兜底（比如.mp4默认video/mp4）
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
            return switch (extension) {
                case ".mp4" -> "video/mp4";
                case ".avi" -> "video/x-msvideo";
                case ".mov" -> "video/quicktime";
                case ".mkv" -> "video/x-matroska";
                default -> "application/octet-stream"; // 未知类型
            };
        }
    }


    @Override
    public Video getVideo(Long id) {
        return videoMapper.findVideoById(id);
    }

    @Override
    public Video getVideo(String vid) {
        return videoMapper.findVideoByVid(vid);
    }

    @Override
    public Object findById(Long id) {
        return null;
    }

    @Override
    public Object save(Object o) {
        return null;
    }

    @Override
    public Object deleteById(Long id) {
        return null;
    }

    @Override
    public boolean update(Object o) {
        return false;
    }
}
