<template>
    <div class="video-container">
        <h1>视频页面</h1>
        <p>这里是视频播放和管理的页面。</p>
        <div>
            <p>当前视频名称：{{ videoName }}</p>
            <input type="text" v-model="video"/>
            <button @click="updateVideo" id="btn">更新视频</button>
        </div>
        <!-- <video controls :src="`http://localhost:8080/test/videoRange/${videoName}.mp4`" width="800" min-width="300">
            您的浏览器不支持 video 标签。
        </video> -->

        <video controls :src="`http://localhost:8080/video/VMfengjin1`" width="800" min-width="300">
            您的浏览器不支持 video 标签。
        </video>
        
    </div>
</template>


<script setup>
    import { ref } from 'vue';

    const videoName = ref("fengjin");
    const video = ref("fengjin");

    const updateVideo = () => {
        videoName.value = video.value;
    };
    

</script>


<style scoped>
    #btn{
        color:white;
        background-color: aqua;
    }
</style>