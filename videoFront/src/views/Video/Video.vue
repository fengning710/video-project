<template>
  <div class="video-container">
    <h1>视频页面</h1>
    <p>这里是视频播放和管理的页面。</p>
    
    <div>
      <p>当前视频ID/VID：{{ currentVideoId }}</p>
      <input 
        type="text" 
        v-model="inputValue" 
        placeholder="输入视频ID或VID（如1/VMfengjin1）"
      />
      <button @click="goToVideo" id="btn">跳转播放</button>
    </div>

    <!-- 视频标签：确保src绑定正确，样式可见 -->
    <video 
      controls 
      :src="videoPlayUrl"
      width="800" 
      min-width="300"
      style="display: block; margin-top: 20px; border: 1px solid #eee;"
      v-if="videoPlayUrl"
    >
      您的浏览器不支持 video 标签。
    </video>
    <p v-else-if="loading">视频加载中...</p>
    <p v-else class="error">视频未加载或加载失败</p>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '../../api/request'; // 你的request工具

// 1. 先定义路由相关（最先初始化）
const route = useRoute();
const router = useRouter();

// 2. 定义响应式变量
const inputValue = ref('');
const currentVideoId = ref('');
const videoPlayUrl = ref('');
const loading = ref(false); // 加载状态

// 3. 先定义 fetchVideo 函数（在 watch 之前）
const fetchVideo = async (videoId) => {
  loading.value = true;
  videoPlayUrl.value = ''; // 重置播放地址
  try {
    const res = await request({
      url: `/video/${videoId}`, // 后端接口：@GetMapping("/video/{videoId}")
      method: 'get',
      responseType: 'blob', // 必须加：解析视频流
      headers: { 'Content-Type': 'application/octet-stream' }
    });
    // 把视频流转成可播放的URL
    videoPlayUrl.value = URL.createObjectURL(res.data);
    currentVideoId.value = videoId; // 更新当前视频ID
  } catch (err) {
    console.error('视频加载失败：', err);
    videoPlayUrl.value = '';
    currentVideoId.value = '加载失败';
    alert('视频加载失败：' + (err.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

// 4. 定义跳转函数
const goToVideo = () => {
  const val = inputValue.value.trim();
  if (val) {
    router.push(`/video/${val}`);
    inputValue.value = '';
  } else {
    alert('请输入有效视频ID或VID！');
  }
};

// 5. 最后定义 watch 监听（此时 fetchVideo 已定义）
watch(
  () => route.params.videoId, // 监听路由的 videoId 变化
  (newVideoId) => {
    if (newVideoId) {
      fetchVideo(newVideoId); // 现在能正常访问 fetchVideo 了
    }
  },
  { immediate: true } // 页面加载时立即执行
);

// 6. 可选：页面初始化时再次确认（双重保障）
onMounted(() => {
  const initialId = route.params.videoId;
  if (initialId) {
    fetchVideo(initialId);
  }
});
</script>

<style scoped>
.video-container {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 40px);
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