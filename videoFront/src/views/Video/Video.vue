<template>
  <div class="video-container">
    <h1>视频页面</h1>
    <p>这里是视频播放和管理的页面。</p>
    
    <div>
      <p>当前视频ID/VID：{{ currentVideoId || '未选择' }}</p>
      <input 
        type="text" 
        v-model="inputValue" 
        placeholder="输入视频ID或VID（如1/VMfengjin1）"
      />
      <button @click="goToVideo" id="btn">跳转播放</button>
    </div>

    <!-- 核心：video直接对接后端接口，无中间层 -->
    <video 
      controls 
      playsinline
      :src="currentVideoId ? `http://192.168.81.48:8080/video/${currentVideoId}` : ''"
      width="800" 
      min-width="300"
      style="display: block; margin-top: 20px; border: 1px solid #eee;"
      v-if="currentVideoId"
      @progress="handleProgress"
      @loadedmetadata="handleLoadedMetadata"
    >
      您的浏览器不支持 video 标签，请升级浏览器。
    </video>
    <p v-else-if="loading">视频加载中...</p>
    <p v-else class="error">请输入有效视频ID并点击跳转播放</p>
  </div>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  // 1. 路由与响应式变量（完全保留原页面逻辑）
  const route = useRoute();
  const router = useRouter();
  const inputValue = ref('');
  const currentVideoId = ref('');
  const loading = ref(false);

  // 2. 跳转播放功能（和原来完全一致）
  const goToVideo = () => {
    const val = inputValue.value.trim();
    if (val) {
      router.push(`/video/${val}`);
      inputValue.value = ''; // 清空输入框
    } else {
      alert('请输入有效视频ID或VID！');
    }
  };

  // 3. 监听路由变化，更新视频ID（核心简化：只更ID，不做额外请求）
  watch(
    () => route.params.videoId,
    (newVideoId) => {
      if (newVideoId) {
        loading.value = true;
        currentVideoId.value = newVideoId;
        // video.src会自动更新，浏览器发起原生请求（含Range）
        loading.value = false;
      } else {
        currentVideoId.value = '';
      }
    },
    { immediate: true } // 组件加载时立即执行
  );

  // 4. 可选：播放进度监听（查看缓冲状态）
  const handleProgress = (e) => {
    const video = e.target;
    const buffered = video.buffered;
    if (buffered.length > 0) {
      const bufferedEnd = buffered.end(buffered.length - 1);
      const duration = video.duration;
      console.log(`已缓冲：${(bufferedEnd / duration * 100).toFixed(1)}%`);
    }
  };

  // 5. 可选：视频元数据加载完成（显示总时长）
  const handleLoadedMetadata = (e) => {
    const video = e.target;
    const duration = video.duration;
    const minutes = Math.floor(duration / 60);
    const seconds = Math.floor(duration % 60);
    console.log(`视频总时长：${minutes}分${seconds}秒`);
  };
</script>

<style scoped>
  /* 完全保留原页面样式，无任何修改 */
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