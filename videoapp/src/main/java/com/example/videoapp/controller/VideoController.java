package com.example.videoapp.controller;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.model.dto.VideoStreamInfo;
import com.example.videoapp.model.dto.VideoUploadDTO;
import com.example.videoapp.model.entity.Video;
import com.example.videoapp.model.vo.PageResult;
import com.example.videoapp.model.vo.VideoVO;
import com.example.videoapp.service.VideoService;
import com.example.videoapp.util.JwtUtils;
import com.example.videoapp.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// 视频相关和前端对接的接口类
@RestController
public class VideoController {
    /**
     * 视频业务层服务类依赖注入
     * 通过@Autowired从Spring IOC容器中注入已初始化的VideoService Bean，用于视频相关业务操作
     */
    @Autowired
    private VideoService videoService;

    // 测试使用
    private static final Logger log = LoggerFactory.getLogger(VideoController.class);


    // 项目核心：视频播放接口
    @GetMapping("/video/{videoId}")
    public void playVideo(
            @PathVariable String videoId,   // 接收URL路径参数
            // 请求对象：获取请求头/客户端信息（测试）
            HttpServletRequest request,
            // 响应对象：设置响应头/响应体/状态码
            // 注：此处主要是设置响应体为分片视频流，状态码为206（支持分片播放）
            HttpServletResponse response
    ){
        // 测试使用（打印日志）
//        String rangeHeader1 = request.getHeader("Range");
//        System.out.println("收到的Range头：" + (rangeHeader1 == null ? "null" : rangeHeader1));

        // 测试使用（移动端适配测试使用，打印日志）
        /*// 1. 打印客户端信息（是否是移动端）
        String userAgent = request.getHeader("User-Agent");
        // 2. 打印核心请求头（Range=分片范围，Host=请求地址）
        String range = request.getHeader("Range");
        String host = request.getHeader("Host");
        // 3. 打印客户端IP
        String clientIp = request.getRemoteAddr();


        // 日志输出（关键信息一目了然）
        log.info("=== 视频播放请求详情 ===");
        log.info("客户端IP：{}", clientIp);
        log.info("客户端类型（User-Agent）：{}", userAgent);
        log.info("请求Range：{}", range);
        log.info("请求Host：{}", host);
        log.info("请求视频ID：{}", videoId);*/

        // 双层try：内层try-with-resource自动视频流等资源（避免泄漏），外层统一处理异常
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

            // 项目核心逻辑：分片传输视频供前端播放
            // 内层try-with-resource可自动关闭资源
            try(
                    // 开启输入输出流
                    InputStream fis = new FileInputStream(info.getFilePath());
                    OutputStream os = response.getOutputStream()
                    ){
                // 跳过要传的字节
                fis.skip(info.getStart());

                //设置缓冲区，减少磁盘读写次数
                byte[] buffer = new byte[4096]; // 4MB，也可设置更大（如8MB）
                // 设置每一个读取的字节
                int bytesRead;
                // 获取所有需要读取的字节数（使用long类型防止数值超出21亿）
                long remainRead = info.getContentLength();
                // 判断 当剩余读写字节数 以及 没读到最后一个字节 时循环继续
                while (remainRead > 0 && (bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    os.flush(); // 立即发送，避免缓存
                    remainRead -= bytesRead;  // 更新剩余字节
                }

            }
        }catch (Exception e){
            String exceptionMeg = e.getMessage() == null ? "" : e.getMessage();
            if(e instanceof IOException){ // 判断异常是否属于io异常/其子类
                if(e.getMessage().contains("中止了一个已建立的连接")){
                    // 此处处理前端（如用户拉进度条，用户快速切视频）导致的io中断异常
                    // 不发统一异常，仅后端打印信息处理
                    System.out.println("无需在意，不是什么错误--" + exceptionMeg);
                }else{
                    // 此处处理其他io异常
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"视频读取失败(io)");
                }
                //以下是对于在service抛出的异常的特定处理
            }else if(e instanceof BusinessException){
                //抛出原异常，避免因转换而丢失异常数据
                    throw (BusinessException)e;
            }else{
                // 抛统一异常处理
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, exceptionMeg);
            }
        }

    }

    // 查视频信息接口
    @GetMapping("/video/info/{videoId}")
    public Result<VideoVO> getVideoInfoCon(@PathVariable String videoId){
        return Result.success(videoService.getVideoInfo(videoId));
    }


    // 列表查视频接口
    @GetMapping("/videos/list")
    public Result<PageResult> getVideoPageList(
            // 3个可选参数
            // 注解表示接收拼接在URL后的同名数据
            // 如 (/videos/list?keyword="pv")中，keyword变量接收到值为"pv"
            @RequestParam(required = false) Long pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword
    ){
        return Result.success(videoService.getVideoPageList(pageNum, pageSize, keyword, null));
    }

    // 列表查视频接口
    // 用户个人主页使用
    @GetMapping("/user/videos/list")
    public Result<PageResult> getUserVideoPageList(
            @RequestParam(required = false) Long pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long userId,
            HttpServletRequest request
    ){
        // 从请求对象取token(前端规定字段)
        String token = request.getHeader("Authorization");
        // 初步处理token
        if(token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        // 通过jwt工具类的静态方法获取userId，如果解析不了则返回null
        userId = JwtUtils.getUserId(token);
        // 如果userId为空则抛异常
        if(userId == null){
            throw new BusinessException(ErrorCode.USER_WRONG_TOKEN);
        }
        // userId非空时调Service层方法查用户上传的视频列表
        return Result.success(videoService.getVideoPageList(pageNum, pageSize, keyword, userId));
    }

    // 项目核心：视频上传接口
    @PostMapping("/video/upload")
    public Result<Video> uploadVideo(VideoUploadDTO uploadDTO, HttpServletRequest request){
        try{
            // 同上方逻辑（后续考虑整合）
            String token = request.getHeader("Authorization");
            if(token != null && token.startsWith("Bearer ")){
                token = token.substring(7);
            }
            Long userId = JwtUtils.getUserId(token);
            if(userId != null){
                // 解析出 userId 后，直接赋给上传视频传输类
                // 不需用户手动输入userId，防止越权上传
                uploadDTO.setUserId(userId);
            }else{
                throw new BusinessException(ErrorCode.PARAM_ERROR, "上传用户错误");
            }
            return Result.success(videoService.uploadVideo(uploadDTO));
        }catch (BusinessException e){  // 捕获到前面抛的自定义异常直接抛出
            throw e;
        }catch (Exception e){  // 捕获到其他异常后处理成自定义异常抛出
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "视频上传失败：" + e.getMessage());
        }
    }
}
