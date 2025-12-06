<!-- 核心：视频播放页面组件 -->
<template>
    <div class="video-container">
        <h1>视频页面</h1>
        <p>这里是视频播放和管理的页面。</p>
        
        <div>
            <p>当前视频id/vid：{{ currentVideoId || '未选择' }}</p>
            <input 
                type="text" 
                v-model="inputValue" 
                placeholder="输入视频id或vid（如1/VMfengjin1）"
            />
            <button @click="goToVideo" id="btn">跳转播放</button>
        </div>

        <!-- 视频资料渲染 -->
        <div v-if="currentVideoId" style="margin: 20px 0; padding: 15px; border: 1px solid #eee; border-radius: 8px;">
            <h2 style="margin: 0 0 8px 0; color: #5650a8;">{{ currentVideoInfo.title || '视频消失了呢' }}</h2>
            <p style="margin: 0 0 4px 0; color: #666;">作者：{{ currentVideoInfo.author || '未知作者' }}</p>
            <!-- 视频创建时间展示 -->
            <p style="margin: 0; color: #999; font-size: 14px;">上传时间：{{ formatTime(currentVideoInfo.createdTime) || '未知时间' }}</p>
        </div>

        <!-- 使用原生video标签控制播放（可配合后端进行分片播放） -->
        <video 
            controls 
            playsinline
            :src="currentVideoId ? `http://localhost:8080/video/${currentVideoId}` : ''"
            width="800" 
            min-width="300"
            style="display: block; margin-top: 20px; border: 1px solid #eee;"
            v-if="currentVideoId"
            @progress="handleProgress"
            @loadedmetadata="handleLoadedMetadata"
            id="videoPlay"
        >
            您的浏览器不支持 video 标签，请升级浏览器。
        </video>
        
        <!-- 视频描述展示区域（视频下方） -->
        <div v-if="currentVideoId" style="margin: 15px 0; padding: 15px; border: 1px solid #eee; border-radius: 8px; max-width: 800px;">
            <h3 style="margin: 0 0 8px 0; color: #5650a8; font-size: 16px;">视频介绍</h3>
            <p style="margin: 0; color: #666; line-height: 1.6;">{{ currentVideoInfo.description || '视频还没有描述哦~' }}</p>
        </div>

        <p v-else-if="loading">视频加载中...</p>
        <p v-else class="error">请输入有效视频ID并点击跳转播放</p>
    </div>
</template>

<script setup>
    import { ref, watch, onMounted } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import request from '../../api/request';
    // 导入视频详情API（已整合的api）
    import { getVideoInfo } from '../../api/modules/videoApi';

    const route = useRoute();
    const router = useRouter();
    const inputValue = ref('');
    const currentVideoId = ref('');
    const loading = ref(false);

    const currentVideoInfo = ref({
        title: "",
        author: "",
        // 创建时间字段（与后端响应对齐）
        createdTime: "",
        // 描述字段（与后端响应对齐）
        description: ""
    });


    // 按钮跳转视频函数
    const goToVideo = () => {
        const val = inputValue.value.trim();
        if (val) {
            router.push(`/video/${val}`);
            inputValue.value = '';
        } else {
            alert('请输入有效视频id或vid！');
        }
    };

    // 格式化时间函数（处理后端返回的ISO格式时间）
    const formatTime = (timeStr) => {
        if (!timeStr) return '';
        const date = new Date(timeStr);
        // 格式：2025-11-30 18:59:34（补零处理，确保格式统一）
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`;
    };

    /** 监测数据变化函数
     *  如果videoId（vid或id）改变，
     *  就执行后续获取到新的videoId的方法
     *  后续方法异步执行，用作获取对应videoId视频的信息
     */
    watch(
        () => route.params.videoId,
        async (newVideoId) => {
            if (newVideoId) {
                loading.value = true;
                currentVideoId.value = newVideoId;
                try {
                    // 调用视频详情API
                    const res = await getVideoInfo(newVideoId);
                    // 直接赋值后端返回的字段（确保与VideoVO响应对齐）
                    currentVideoInfo.value.title = res.title || '未知标题';
                    currentVideoInfo.value.author = res.author || '未知作者';
                    currentVideoInfo.value.createdTime = res.createdTime || '';
                    currentVideoInfo.value.description = res.description || '';
                } catch (err) {
                    console.error('获取视频详情失败', err);
                    // 错误时重置信息，避免残留数据
                    currentVideoInfo.value = { 
                        title: "", 
                        author: "",
                        createdTime: "",
                        description: "" 
                    };
                } finally {
                    loading.value = false;
                }
            } else {
                currentVideoId.value = '';
                currentVideoInfo.value = { 
                    title: "", 
                    author: "",
                    createdTime: "",
                    description: "" 
                };
            }
        },
        { immediate: true }
    );

    onMounted(() => {
        
    })

    // 控制台显示缓冲进度
    const handleProgress = (e) => {
        const video = e.target;
        const buffered = video.buffered;
        if (buffered.length > 0) {
            const bufferedEnd = buffered.end(buffered.length - 1);
            const duration = video.duration;
            console.log(`已缓冲：${(bufferedEnd / duration * 100).toFixed(1)}%`);
        }
    };

    // 控制台显示视频时长（视频流计算）
    const handleLoadedMetadata = (e) => {
        const video = e.target;
        const duration = video.duration;
        const minutes = Math.floor(duration / 60);
        const seconds = Math.floor(duration % 60);
        console.log(`视频总时长：${minutes}分${seconds}秒`);
    };
</script>

<style scoped>
    .video-container {
        position: fixed;
        top: 100px;
        left: 0;
        right: 0;
        padding: 20px;
        background: #fff;
        min-height: auto;
        max-height: calc(100vh - 120px);
        box-sizing: border-box;
        overflow-y: auto;
        z-index: 1;
    }
    #btn {
        color: white;
        background-color: aqua;
        margin-left: 10px;
        padding: 4px 12px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }
    #btn:hover {
        background-color: #00ced1;
    }
    input {
        padding: 4px 8px;
        width: 250px;
        border: 1px solid #ddd;
        border-radius: 4px;
    }
    .error {
        color: #ff4444;
        margin-top: 20px;
    }
    
</style>