package com.example.videoapp.controller;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.model.dto.VideoStreamInfo;
import com.example.videoapp.model.dto.VideoUploadDTO;
import com.example.videoapp.model.entity.Video;
import com.example.videoapp.model.vo.PageResult;
import com.example.videoapp.service.VideoService;
import com.example.videoapp.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
public class VideoController {
    @Autowired
    private VideoService videoService;

    @GetMapping("/video/{videoId}")
    public void playVideo(@PathVariable String videoId, HttpServletRequest request, HttpServletResponse response){
        try{
            // 获取请求头
            String rangeHeader = request.getHeader("Range");
            // 获取视频各项信息
            VideoStreamInfo info = videoService.getVideoStream(videoId, rangeHeader);

            //设置响应头
            response.setContentType(info.getMimeType());
            response.setContentLengthLong(info.getContentLength());
            response.setHeader("Accept-Ranges","bytes");
            response.setHeader("Content-Range", "bytes " + info.getStart() + "-" + info.getEnd() + "/" + info.getFileTotalLength());
            // 设置部分传输响应码(206)
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setContentLengthLong(info.getEnd() - info.getStart() + 1);

            String contentRangeHeader = "bytes " + info.getStart() + "-" + info.getEnd() + "/" + info.getFileTotalLength();
            System.out.println("Setting Content-Range Header: " + contentRangeHeader); // 打印出来检查
            response.setHeader("Content-Range", contentRangeHeader);

            // 流传输
            try(
                    InputStream fis = new FileInputStream(info.getFilePath());
                    OutputStream os = response.getOutputStream()
                    ){
                // 跳过要传的字节
                fis.skip(info.getStart());

                //设置缓冲区
                byte[] buffer = new byte[4096];
                int bytesRead;
                long remainRead = info.getContentLength();
                while (remainRead > 0 && (bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    os.flush(); // 立即发送，避免缓存
                    remainRead -= bytesRead;  // 更新剩余字节
                }

            }
        }catch (Exception e){
            String exceptionMeg = e.getMessage() == null ? "" : e.getMessage();
            if(e instanceof IOException){
                if(e.getMessage().contains("中止了一个已建立的连接")){
                    System.out.println("无需在意，不是什么错误--" + exceptionMeg);
                }else{
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"视频读取失败(io)");
                }
                //以下新增对于在service抛出的异常的特定处理
            }else if(e instanceof BusinessException){
                    throw (BusinessException)e;
            }else{
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, exceptionMeg);
            }
        }

    }


    @GetMapping("/videos/list")
    public Result<PageResult> getVideoPageList(
            @RequestParam(required = false) Long pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword
    ){
        return Result.success(videoService.getVideoPageList(pageNum, pageSize, keyword));
    }

    @PostMapping("/video/upload")
    public Result<Video> uploadVideo(VideoUploadDTO uploadDTO){
        try{
            return Result.success(videoService.uploadVideo(uploadDTO));
        }catch (BusinessException e){
            throw e;
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "视频上传失败：" + e.getMessage());
        }
    }
}
