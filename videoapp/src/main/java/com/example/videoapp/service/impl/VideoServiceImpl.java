package com.example.videoapp.service.impl;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.mapper.VideoMapper;
import com.example.videoapp.model.dto.VideoStreamInfo;
import com.example.videoapp.model.entity.Video;
import com.example.videoapp.model.vo.PageResult;
import com.example.videoapp.model.vo.VideoVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.videoapp.model.vo.VideoVO.change;

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

        // 添加最终文件名存储，方便分2种方法查询
        String fileNameHad = null;

        // 记得添加按id查找的逻辑（用id查文件）
        if(fileName == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "输入视频id或vid为空");
        }
        //判断是否VM(大小写不敏感)开头，是的转vid查询
        if(fileName.regionMatches(true,0,"VM",0,2) && fileName.length() > 6){
            //转前缀，防止出现小写情况
            String prefix = fileName.substring(0, 2).toUpperCase();
            String suffix = fileName.substring(2);
            fileName = prefix + suffix;
            fileNameHad = videoMapper.findVideoByVid(fileName).getFilePath();
        }

        if(fileNameHad == null){
            try{
                Long DataBaseId = Long.valueOf(fileName);
                if(DataBaseId <= 0){
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "id为负");
                }
                System.out.println("视频id方式访问，id为" + DataBaseId);
                fileNameHad = videoMapper.findVideoById(DataBaseId).getFilePath();
            }catch (Exception e){
                throw new BusinessException(ErrorCode.PARAM_ERROR, "视频id或vid出错");
            }
        }

        Path videoPath = Paths.get(videoBasePath).resolve(fileNameHad);
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
    public PageResult<VideoVO> getVideoPageList(Long pageNum, Integer pageSize, String keyword) {
        if(pageNum == null || pageNum < 1){pageNum = 1l;}
        if(pageSize == null || pageSize < 1 || pageSize > 50 ){pageSize = 10;}
        if(keyword == null){keyword = "";}

        // 拼接模糊查询关键词（mybatis要求语法）
        String searchKeyword = "%" + keyword + "%";

        // 查数据库
        List<Video> videoList = videoMapper.findVideoList(searchKeyword);
        Long total = videoMapper.findVideoTotal(searchKeyword);

        // 计算各项数据
        long totalPage = (total + pageSize - 1) / pageSize;// 标准向上取整写法
          // 起始下标
        int startIndex = (int)(pageSize * (pageNum - 1));
          //结束下标，防止超总条数
        int endIndex = (int)Math.min(startIndex + pageSize, total);

        // 截取视频列表
        List<Video> pageVideoList = new ArrayList<>();
        if(startIndex < total){
            pageVideoList = videoList.subList(startIndex, endIndex);
        }

        // 处理视频
        List<VideoVO> pageVideoVOList = new ArrayList<>();
        for(Video video : pageVideoList){
            VideoVO pageVideoVO = change(video);
            pageVideoVOList.add(pageVideoVO);
        }

        // 建返回列表
        PageResult<VideoVO> pageResult = new PageResult<>(
                pageVideoVOList,total,totalPage,pageNum,pageSize
        );
        return pageResult;
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
