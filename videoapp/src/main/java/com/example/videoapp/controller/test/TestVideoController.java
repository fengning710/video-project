package com.example.videoapp.controller.test;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
public class TestVideoController {
    //写死视频根目录
//    private static final String VIDEO_BASE_PATH = "D:\\document\\videoResource\\";

    private final String VIDEO_BASE_PATH;

    public TestVideoController(@Value("${videoArea.baseUrl}") String videoAreaBaseUrl) {
        this.VIDEO_BASE_PATH = videoAreaBaseUrl;
    }

    @GetMapping("/test/video/{filename}")
    public void playVideo(@PathVariable String filename, HttpServletResponse response){
        String filePath = VIDEO_BASE_PATH + filename;
        File file = new File(filePath);

        if(!file.exists() || !file.isFile()){
            throw new BusinessException(ErrorCode.VIDEO_NOT_FOUND);
        }

        byte[] buffer = new byte[4096];
        int len;

        //设返回头文件格式
        String minitype = getMimeType(filename);
        response.setContentType(minitype);
        try(
                InputStream in = new FileInputStream(file);
                OutputStream out = response.getOutputStream()
                ){
                    while((len = in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                    out.flush();
        }catch(Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    private String getMimeType(String fileName){
        String ext = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        switch (ext){
            case "mp4" -> {
                return "video/mp4";
            }
            case "avi" -> {
                return "video/x-msvideo";
            }
            case "mkv" -> {
                return "video/x-matroska";
            }
            default -> {
                return "application/octet-stream";
            }
        }
    }

    @GetMapping("/test/videoRange/{filename}")
    public void playVideoRange(@PathVariable String filename, HttpServletRequest request, HttpServletResponse response) {
        String filePath = VIDEO_BASE_PATH + filename;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new BusinessException(ErrorCode.VIDEO_NOT_FOUND);
        }

        try{
            long fileLength = file.length();
            String mimeType = getMimeType(filename);

            String rangeHeader = request.getHeader("Range");
            if(rangeHeader == null || !rangeHeader.startsWith("bytes=")){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "全量传输");
            }

            String range = rangeHeader.replace("bytes=","");
            String[] rangeParts = range.split("-");
            long start = Long.parseLong(rangeParts[0]);
            long end = rangeParts.length > 1 && !rangeParts[1].isEmpty()
                    ? Long.parseLong(rangeParts[1])
                    : fileLength - 1;

            if(start < 0 || start > end || end >= fileLength){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"分片范围出错");  // 记得添加相应错误枚举
            }

            response.setContentType(mimeType);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206状态码
            response.setHeader("Accept-Ranges","bytes");
            response.setHeader("Content-Range","bytes " + start + "-" + end + "/" + fileLength);
            response.setContentLengthLong(end - start + 1);
            try(
                    InputStream in = new FileInputStream(file);
                    OutputStream out = response.getOutputStream()
                    ){
                in.skip(start);
                byte[] buffer = new byte[4096];
                int bytesRead;
                long remaining = end - start + 1;
                while(remaining > 0 && (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1){
                    out.write(buffer, 0, bytesRead);
                    remaining -= bytesRead;
                }
                out.flush();
            }
        }catch(Exception e){
            if(e instanceof IOException){
                if(e.getMessage().contains("中止了一个已建立的连接")){
                    System.out.println("无需在意，不是什么错误--"+e.getMessage());
                }else{
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"io问题");
                }
            }else{
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
            }

        }

    }
}
