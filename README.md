# VideoApp 前后端分离项目  
![GitHub](https://img.shields.io/badge/GitHub-公开仓库-brightgreen)
![技术栈](https://img.shields.io/badge/后端-SpringBoot3%2Ex-red)
![技术栈](https://img.shields.io/badge/前端-Vue3-pink)
![MySQL 版本](https://img.shields.io/badge/MySQL-8%2E0+-green)


## 项目简介  
### 1. 项目背景
本项目为课程设计，聚焦「视频平台」场景，实现从用户注册登录到视频上传、播放的全流程，旨在掌握 <b style="color:blue">Java SE</b> 基础，前后端分离开发，**JWT**认证，**Vue**模块化前端页面设计等核心技术。

### 2. 核心功能
| 模块    | 具体功能            |
|--------|----------------------|
| 用户模块| 注册、登录（JWT生成/校验 token）、个人信息展示 |
| 视频上传模块| 视频全量上传、自动解析 token 以设置上传用户 |
| 视频播放模块| 支持分片播放、进度条可自由拖动 |


<hr/>

## 🛠️ 技术栈选型（部署环境）
| 分类       | 技术框架/工具       | 版本要求       |
|------------|--------------------|----------------|
| 后端    | Spring Boot + MyBatis    | Spring Boot 3.x |
| 前端  | Vue 3 + Vue Router + Axios  | Vue 3.x、Node 18+ |
| 数据库  | MySQL  + Navicat   | MySQL 8.0+   |
| 认证    | JWT（JSON Web Token）  | -   |
| 接口测试    | Postman    |    -     |
| 项目管理  | Maven + npm   | Maven 3.8+、npm 9+ |
| 运行环境  | JDK + 浏览器         | JDK 17+、Chrome 90+ |
| 操作系统 | Windows/Linux/macOS |  - |
 
## ✨ 项目核心亮点
- 🚀 **一键部署无门槛**：提供完整运行包，无需配置环境、编译源码，解压+启动两步搞定，新手也能快速验证功能；
- 🎬 **视频播放优化**：基于 HTTP Range 实现分片播放，1080P 大视频加载无卡顿，支持进度条自由拖动；
- 🔐 **安全认证机制**：采用 JWT 无状态登录，密码加密存储，接口权限校验，避免非法访问；
- 🎨 **前后端分离架构**：前端 Vue3 组件化开发，后端 Spring Boot 解耦设计，代码可维护性强；
- 📁 **测试资源齐全**：内置 2 个测试视频，配套数据库脚本，无需额外准备测试数据。


<hr/>

## 部署方式


### 📦 方式1：解压即使用（推荐新手）
<details>
<summary>点击展示详细步骤</summary>

#### 🔧 前置准备
==确保运行设备已安装 **MySQL 8.0+** 数据库，且 8080/3306 端口未被占用==

#### 📥 步骤1️⃣：下载压缩包
项目根目录提供完整运行包下载：  
[VideoApp-项目压缩包.zip](https://bilibili.com)（替换为实际下载链接，如 GitHub Release/网盘）

#### 📂 步骤2️⃣：解压项目结构
解压后完整目录如下（前后端分离结构，清晰区分）：
```plain
VideoApp-项目压缩包/
├── backend/          # 后端运行目录
│   ├── videoapp-0.0.1-SNAPSHOT.jar  # 后端可执行Jar包
│   └── video_db.sql                 # 数据库初始化脚本
├── frontend/         # 前端静态文件目录
│   └── dist/               # 前端打包后的可运行文件
└── test-video/       # 测试视频目录（含2个样例视频）
    ├── 霜降｜傲霜独立，枯荣随风.mp4
    └── 用120秒，爱上这个冰雪世界！.mp4
```
#### 🗄️ 步骤3️⃣：初始化数据库
- 启动本地 MySQL 服务；
- 执行 backend/video_db.sql 脚本（可通过 Navicat/MySQL Workbench / 命令行运行）；
- 验证：数据库中生成 video_app 库及 user/video 表即完成。
#### ▶️ 步骤4️⃣：启动后端
打开终端 / CMD，进入 backend 目录；
执行启动命令（确保本地已安装 JDK 17+）：
```bash
java -jar videoapp-0.0.1-SNAPSHOT.jar
```
✅ 成功标识：终端打印 Started VideoAppApplication in X seconds
#### 🖥️ 步骤5️⃣：启动前端
- 进入 frontend/dist 目录；
- 双击 index.html 直接运行，或用 VS Code 安装「Live Server」插件后右键打开；
- 📝 提示：前端已内置跨域配置，无需额外修改任何文件
#### ✨ 步骤6️⃣：立即开测
打开浏览器访问前端页面（默认本地路径），即可测试「注册 / 登录 / 上传 / 播放」全功能。
</details>


### 📄方式 2：源码运行(省略源码部署)

> ⚠️ 核心提醒：源码部署必须补全隐私配置文件（否则启动会报错），以下步骤缺一不可！

<details>

<summary>点击展示详细步骤</summary>

#### 步骤 1：克隆项目到本地
```bash
# 克隆仓库
git clone https://github.com/Philip-9527666/video-project.git
```

#### 步骤 2：补全隐私配置文件（核心必做）
由于隐私配置（数据库账号 / 密码等）未上传 GitHub，需手动新建配置文件：
- 进入项目后端目录 `src/main/resources/`；
- 新建文件 `application-private.yml`；
- 复制以下模板并替换为自己的环境配置（必填项已标注）：
  ```bash
     # application-private.yml 配置模板（必须填写以下内容）
    spring:
      datasource:
        username: 你的MySQL账号（如root） # 必填
        password: 你的MySQL密码（如123456） # 必填
        
    # 视频存储路径（必填，确保该路径有读写权限）
    videoArea:
      baseUrl: 你的视频存储目录 #（如Windows：D:/video-upload/；Linux：/opt/video-upload/）
  ```

  </details>
<hr/>

## 测试须知
### 🔨1.测试用例
|  测试场景  |   操作步骤  |  预期效果 |
|-----------|---------------|---------|
| 用户注册 | 点击主页右上角登录界面，输入用户名和密码+密码确认，点击注册后即注册完成 | 注册后会跳转登录界面 |
| 用户登录 | 输入用户名和密码后点击登录 | 登录成功会自动跳转主页，右上角出现用户名 |
| 视频上传 | 输入标题，选择视频文件（可选择样例视频或自行准备视频进行上传） | 上传完成会弹出弹窗提示 |
| 视频搜索 | 顶部搜索栏输入标题字样（部分或全部），点击搜索 | 自动跳转搜索页，展示视频列表 |
| 视频播放 | 主页或搜索页点击视频列表 | 自动跳转视频播放页，在点击组件框的`暂停/播放`后播放视频，可自由拉动进度条 |

### ⚠️2.测试注意
- 端口问题：保证后端端口 8080，MySQL 3306端口未被占用，如果占用可修改`application.yml`配置的端口；
- 前端跨域问题：无需担心，后端已经配置`CorsConfig`无需进行其他处理。
- 数据库配置：如果使用Jar包进行测试，需要保证数据库账号/密码为`root/123456`，如果不对应，建议通过源码部署并修改隐私配置 ==*记得编写补充application-private.yml文件，添加对应数据库账号密码 + 视频存储根目录*==

## 项目结构（源码）
```plain
VideoApp  
├── sql/        # 数据库初始化文件
│   └── video_db.sql
├── videoFront/    # 前端
│   ├── ···
│   └── src/
│       ├── api/    # api整合
│       ├── components/   # 通用组件
│       ├── router/   # 路由设置
│       └── views/    # 页面组件
├── videoapp/    # 后端
│   └── src/main/
│       ├── resources/   # 存放yml配置文件和SQL语句.xml文件
│       └── java/*/videoapp/
│                  ├── config/   # 配置包
│                  ├── controller/   # 接口包
│                  ├── exception/    # 异常包
│                  ├── mapper/   # 数据库逻辑包
│                  ├── model/  # 实体类包
│                  ├── service/  # 业务包
│                  ├── util/  # 工具包
│                  └── VideoappApplication.java  # 启动类
├── .gitignore
└── README
```

## 📞联系方式
- 邮箱：`2162472794@qq.com`

## 📄许可
本项目为课程设计项目，仅限学习使用，严禁商用。测试视频版权归原作者（`于龙海影像造梦人`和`中国国家地理`）所有