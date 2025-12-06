package com.example.videoapp.service.impl;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import com.example.videoapp.mapper.UserMapper;
import com.example.videoapp.mapper.VideoMapper;
import com.example.videoapp.model.dto.VideoStreamInfo;
import com.example.videoapp.model.dto.VideoUploadDTO;
import com.example.videoapp.model.entity.User;
import com.example.videoapp.model.entity.Video;
import com.example.videoapp.model.vo.PageResult;
import com.example.videoapp.model.vo.VideoVO;
import com.example.videoapp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.videoapp.model.vo.VideoVO.makeVideoVO;

// 视频业务实现类
@Service
public class VideoServiceImpl implements VideoService {

    // 字段注入
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private UserMapper userMapper;

    // 在配置文件中获取视频存储根目录（配置项目时需要自行编写配置文件）
    @Value("${videoArea.baseUrl}")
    private String videoBasePath;

    // 项目核心逻辑：视频播放——返回视频流
    @Override
    public VideoStreamInfo getVideoStream(String filename, String rangeHeader) throws BusinessException{

        // 验证文件名，防止路径穿越攻击 (非常重要！)
        String fileName = StringUtils.cleanPath(filename);

        // 调用私有方法并且获取文件名（vid和id均适配）
        String fileNameHad = findVideo(fileName).getFilePath();

        // 拼接根目录和文件名 并且建文件类
        Path videoPath = Paths.get(videoBasePath).resolve(fileNameHad);
        File file = videoPath.toFile();

        // 判断文件是否存在
        if (!file.exists() || !file.isFile()) {
            throw new BusinessException(ErrorCode.VIDEO_NOT_FOUND);
        }

        long fileLength = file.length();  //视频长度
        // 建视频流传输类并设置变量值
        VideoStreamInfo info = new VideoStreamInfo();
        info.setFilePath(file.toString());
        info.setFileTotalLength(fileLength);
        info.setMimeType(getContentType(file.toString())); //获取内容类型

        // 解析Range请求头，计算startByte和endByte
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
            // endByte：不能超过总长度-1，不能小于startByte
            endByte = Math.min(endByte, fileLength - 1);
            endByte = Math.max(endByte, startByte);

            // 赋值给info
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

    // 视频播放信息展示——取视频具体信息，返回VideoVO
    @Override
    public VideoVO getVideoInfo(String videoId){
        Video video = findVideo(videoId);
        // 查数据库获取信息
        String author = userMapper.findById(video.getUserId()).getUserName();
        return makeVideoVO(video, author);
    }

    // 取视频文件类型私有方法（格式仅供播放使用）
    private String getContentType(String filePath) {
        try {
            // 用Java NIO的Files工具类自动探测文件类型（支持多种格式）
            return Files.probeContentType(new File(filePath).toPath());
        } catch (IOException e) {
            // 异常时根据后缀名兜底（比如.mp4默认video/mp4）
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
            return switch (extension) {
                case "mp4" -> "video/mp4";
                case "avi" -> "video/x-msvideo";
                case "mov" -> "video/quicktime";
                case "mkv" -> "video/x-matroska";
                default -> "application/octet-stream"; // 未知类型
            };
        }
    }

    // 重要逻辑：处理输入videoId（适配id和vid）私有方法（返回视频实体类）
    private Video findVideo(String fileName){
        Video getVideo = null;

        // 判空
        if(fileName == null || fileName.isEmpty()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "输入视频id或vid为空");
        }

        try{
            // 进行vid判断（匹配VM开头(大小写不敏感)和长度在6-20位）
            if (fileName.regionMatches(true, 0, "VM", 0, 2) && fileName.length() >= 6 && fileName.length() <= 20) {
                // 添加正则并判断格式（限定4-18位除VM的随机字符，方便扩展）
                String vidRegex = "^[0-9a-zA-Z]{4,18}$"; // 正则{}也可换成'+'(限制出现1次以上)
                // 全字符串判断有无非法字符，有则方法返回false
                if(!fileName.matches(vidRegex)){  // String类的根据正则判断的方法
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "视频id或vid出错(vid格式错误)");
                }

                // 调整vm标识的大小写问题
                String prefix = fileName.substring(0, 2).toUpperCase();
                String suffix = fileName.substring(2);
                fileName = prefix + suffix;
                getVideo = videoMapper.findVideoByVid(fileName);
                if (getVideo == null) {
                    throw new BusinessException(ErrorCode.VIDEO_NOT_FOUND);
                }
                return getVideo;
            }

            // 进行id判断
            if(getVideo == null){
                // String转换为Long，不能转换则抛 NumberFormatException 异常
                Long DataBaseId = Long.valueOf(fileName);
                if(DataBaseId <= 0){ // 初步判错
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "视频id或vid出错(id小于1)");
                }
                getVideo = videoMapper.findVideoById(DataBaseId);
                if (getVideo == null) {
                    throw new BusinessException(ErrorCode.VIDEO_NOT_FOUND);
                }
            }
        }catch (Exception e){
            if(e instanceof NumberFormatException){
                // 业务异常：错误参数
                throw new BusinessException(ErrorCode.PARAM_ERROR, "视频id或vid出错(错误参数)");
            }else if(e instanceof BusinessException){
                // 业务异常：数据库找不到相关内容
                throw (BusinessException) e;
            }
            // 系统异常：数据库问题/其他问题
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
        return getVideo;
    }


    // 重要逻辑：列表取视频方法(统一使用，模糊查询/全量查询时赋userId为null)
    @Override
    public PageResult<VideoVO> getVideoPageList(Long pageNum, Integer pageSize, String keyword, Long userId) {
        if(pageNum == null || pageNum < 1){pageNum = 1l;}
        if(pageSize == null || pageSize < 1 || pageSize > 50 ){pageSize = 10;}
        if(keyword == null){keyword = "";}

        // 拼接模糊查询关键词（mybatis要求语法）
        String searchKeyword = "%" + keyword + "%";

        // 查数据库（获取列表视频信息和统计总数）
        List<Video> videoList = videoMapper.findVideoList(searchKeyword, userId);
        Long total = videoMapper.findVideoTotal(searchKeyword, userId);

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

        // 处理视频（隐藏数据库存储的隐私信息）
        List<VideoVO> pageVideoVOList = new ArrayList<>();
        // 增强 for 循环逐个处理列表视频信息并加入视频展示列表
        for(Video video : pageVideoList){
            String author = userMapper.findById(video.getUserId()).getUserName();
            VideoVO pageVideoVO = makeVideoVO(video, author);
            pageVideoVOList.add(pageVideoVO);
        }

        // 建返回列表展示类
        PageResult<VideoVO> pageResult = new PageResult<>(
                pageVideoVOList,total,totalPage,pageNum,pageSize
        );
        return pageResult;
    }


    // 设定上传功能基础数据（从配置文件获取）
    @Value("${video.upload.base-path}")
    private String uploadBasePath;
    @Value("${video.upload.allow-types}")
    private String allowTypes;  // 允许的视频格式
    @Value("${video.upload.max-size}")
    private Long maxSize;  // 设定的最大上传大小

    // 设定字符池(大小写字母+数字)，用作随机生成vid
    private static final String CHARACTER_POOL = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int VID_RANDOM_LENGTH = 8; // 8位随机码+VM前缀组成10位vid


    // 视频上传方法(全量上传)  后续可增加分片上传 + 暂停续传
    @Override
    @Transactional  // 保证文件和数据库一致性
    public Video uploadVideo(VideoUploadDTO uploadDTO) {
        // 提取参数信息
        MultipartFile file = uploadDTO.getFile();
        Long userId = uploadDTO.getUserId();
        String title = uploadDTO.getTitle();
        String description = uploadDTO.getDescription();

        // 检查视频和上传用户id
        if(file == null || file.isEmpty()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "上传视频为空");
        }
        if(userId == null){  // 基本不会用到，在Controller层解析 userId 时就已经判断
            throw new BusinessException(ErrorCode.PARAM_ERROR, "上传时用户id为空");
        }

        // 处理标题，如果用户没传则使用原始文件名(去除后缀)
        String originalFileName = file.getOriginalFilename();
        int suffixIndex = originalFileName.lastIndexOf('.');
        title = StringUtils.hasText(title)
                ? title
                : (suffixIndex > 0
                    ? originalFileName.substring(0, suffixIndex)
                    : originalFileName);
        /* 解释：这里两个三目运算符
            1.判断用户传入标题是否为空，非空时使用原来标题，为空时使用上传文件名(转到下个运算)
            2.判断原来文件命名是否有后缀，有则去后缀，没有则直接使用原来文件名
         */

        // 检验标题参数要求
        if(title.length() > 100){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "上传视频标题过长（超过100字符）");
        }
        // 向传输类设标题
        uploadDTO.setTitle(title);

        // 校验文件（大小、格式）
        validateVideoFile(file);
        // 校验用户是否存在
        validateUploadUser(userId);

        // 生成视频vid
        String vid= createVid();
        // 极端情况重试
        int retryCount = 0;
        // 当vid存在 且 重试次数小于5时可重试
        while(checkVidExists(vid) && retryCount < 5){
            vid = createVid();
            retryCount++;
        }
        // 如果6次生成都不成功（概率基本为0，除非是B站等大项目），报错
        if(checkVidExists(vid)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "vid生成错误，请重试");
        }

        // 处理文件存储
          // 获取文件后缀(格式)
        String fileSuffix = StringUtils.getFilenameExtension(originalFileName);
          // 构建文件存储路径(拼接根目录 + 对应系统的目录分隔符 + vid命名的文件名)
        String filePath = vid + "." + fileSuffix;
        String fileSavePath = uploadBasePath + filePath;
        
          // 确保目录存在，不存在则创建(保证首次上传不出错)
        File savePath = new File(uploadBasePath);
        if(!savePath.exists()){  // 判断所有级别目录是否存在
            savePath.mkdirs();  // 不存在则全部创建
        }

        // 保存视频到本地
        try{
            // Spring框架提供的封装了io流的创建本地文件方法
            file.transferTo(new File(fileSavePath));
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "视频存储失败：" + e.getMessage());
        }

        // 创建video实体对象，准备入库
        Video video= buildVideo(vid, filePath, title, userId, description);

        // 存入数据库（失败会触发@Transactional回滚，删除已存文件）
        int insertCount = videoMapper.insertVideo(video);
        if(insertCount != 1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "视频信息存入数据库失败");
        }
        // 返回实体视频类（没有id）
        return video;
    }

    // 重要方法：随机生成vid
    @Override
    public String createVid(){
        // 线程安全
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 获取字符池的总长（62），供随机数生成使用
        int poolLength = CHARACTER_POOL.length();
        // 建可变字符串，方便拼接，减少字符串多次生成造成空间占用大
        StringBuilder vid = new StringBuilder();
        // 加vid统一标识符
        vid.append("VM");
        // 循环次数为设定的随机字符长度（8）
        for(int i = 0; i < VID_RANDOM_LENGTH; i++){
            int randomIndex = random.nextInt(poolLength); // 随机取索引
            vid.append(CHARACTER_POOL.charAt(randomIndex)); //拼接字符
        }
        // 转换可变字符串（StringBuilder）对象为字符串（String）对象
        return vid.toString();
    }

    // 查询vid对应的视频是否存在（vid查重）
    @Override
    public boolean checkVidExists(String vid){
        // 查数据库
        Video video = videoMapper.findVideoByVid(vid);
        // 查到则返回false（ video不为空，经 != 判断为 false），查不到则返回 true
        return video != null;
    }

    // 验证视频信息（参数为 Spring 框架（spring-web 模块）专门为文件上传设计的核心接口对象）
    private void validateVideoFile(MultipartFile file){
        // 验空
        if(file.isEmpty()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "上传视频为空");
        }
        // 验证视频文件大小
        if(file.getSize() > maxSize){
            throw new BusinessException(ErrorCode.VIDEO_TOO_MAX);
        }
        // 验证视频格式
        String fileType = StringUtils.getFilenameExtension(file.getOriginalFilename());
        List<String> allowTypesList = Arrays.asList(allowTypes.split(","));
        if(!allowTypesList.contains(fileType.toLowerCase())){
            throw new BusinessException(ErrorCode.VIDEO_INVALID_TYPE);
        }
    }

    // 校验上传视频的用户是否存在  （防止有人直接通过Postman等发送请求的工具直接上传）
    private void validateUploadUser(Long userId){
        User user = userMapper.findById(userId);
        if(user == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "上传视频的用户不存在");
        }
    }

    // 建视频实体类方法（视频上传使用）
    private Video buildVideo(String vid, String fileSavePath,
                             String title, Long userId,  String description){
        // Lombok 的 @Builder 注解 支持
        // 链式构造（可以不对应构造方法参数顺序，可以不传部分参数）
        // 不受构造方法限制，不需重载多种不同参数的构造方法
        Video video = Video.builder()
                .vid(vid) // 直接给 vid 赋值
                .title(title) // 直接给 title 赋值（必填，漏了会报错）
                .filePath(fileSavePath) // 直接给 filePath 赋值
                .userId(userId) // 直接给 userId 赋值
                .description(description) // 直接赋值 description
                .build();
        return video;
    }



    // 未用到
    @Override
    public Object findById(Long id) {
        return null;
    }

    // 前面有另外的方法实现了
    @Override
    public Object save(Object o) {
        return null;
    }

    // 未使用，留待扩展（接口不实现不行）
    @Override
    public Object deleteById(Long id) {
        return null;
    }

    @Override
    public boolean update(Object o) {
        return false;
    }
}
