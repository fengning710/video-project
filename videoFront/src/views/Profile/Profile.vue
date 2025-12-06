<!-- 个人主页组件 -->
<template>
    <div class="container" style="width: 1200px; margin: 0 auto; padding: 20px 0;">
        <!-- 个人信息模块（极简样式） -->
        <div class="user-info" style="margin: 20px 0; padding: 20px; border-bottom: 1px solid #eee;">
            <h2 style="margin: 0 0 15px 0; color: #333;">个人中心</h2>
            <p style="margin: 5px 0; color: #666;">昵称：{{ userInfo.userName || '测试用户' }}</p>
            <p style="margin: 5px 0; color: #666;">注册时间：{{ userInfo.createTime || '2025-01-01' }}</p>
        </div>

        <!-- 我的视频列表（适配VideoVO，显示author） -->
        <div class="video-list" style="display: flex; flex-wrap: wrap; gap: 25px; padding: 10px 0;">
            <div v-for="video in videoList" :key="video.id" class="video-item" style="width: 280px; border: 1px solid #eee; border-radius: 8px; padding: 15px; box-sizing: border-box;">
                <h4 style="margin: 0 0 12px 0; font-size: 16px; color: #333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{ video.title }}</h4>
                <p style="margin: 0 0 10px 0; font-size: 14px; color: #999;">作者：{{ video.author }}</p>
                <!-- 修复视频地址绑定：拼接基础路径和filePath -->
                <div style="position: relative; width: 100%; height: 160px; background: #f5f5f5; border-radius: 4px; overflow: hidden;">
                    <video :src="`http://localhost:8080/video/${video.vid}`" controls style="width: 100%; height: 100%; object-fit: cover;"></video>
                </div>
                <p style="margin: 12px 0 0 0; font-size: 14px; color: #666; display: -webkit-box; line-clamp: 2; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                    {{ video.description || '无描述' }}
                </p>
            </div>
        </div>

        <!-- 分页部分（适配手动分页逻辑） -->
        <div class="pagination" style="margin: 40px 0; text-align: center;">
            <button 
                @click="handlePageChange(pageNum - 1)" 
                :disabled="pageNum === 1"
                style="padding: 8px 16px; margin: 0 5px; border: 1px solid #42b983; background: #fff; color: #42b983; border-radius: 4px; cursor: pointer;"
            >
                上一页
            </button>
            <span style="margin: 0 10px; color: #666;">
                第 {{ pageNum }} 页 / 共 {{ totalPage }} 页
            </span>
            <button 
                @click="handlePageChange(pageNum + 1)" 
                :disabled="pageNum >= totalPage"
                style="padding: 8px 16px; margin: 0 5px; border: none; background: #42b983; color: #fff; border-radius: 4px; cursor: pointer;"
            >
                下一页
            </button>
        </div>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue';
    import request from '@/api/request'; // 复用已有的请求封装

    // 个人信息
    const userInfo = ref({});
    // 视频列表相关
    const videoList = ref([]);
    const pageNum = ref(1); // 当前页
    const pageSize = ref(10); // 每页条数
    const total = ref(0); // 总条数
    const totalPage = ref(0); // 总页数

    // 查个人信息（后端固定返回，接口不变）
    const getUserInfo = async () => {
        try {
            const res = await request.get('/user/info');
            userInfo.value = res;
        } catch (err) {
            console.error('查个人信息失败：', err);
        }
    };

    // 查我的视频列表（适配接口 /user/videos/list，不用传userId）
    const getUserVideoList = async () => {
        try {
            const res = await request.get('/user/videos/list', {
                params: { pageNum: pageNum.value, pageSize: pageSize.value }
            });
            videoList.value = res.data;
            total.value = res.total;
            totalPage.value = res.totalPage;
        } catch (err) {
            console.error('查我的视频失败：', err);
        }
    };

    // 分页切换
    const handlePageChange = (newPage) => {
        if (newPage < 1 || newPage > totalPage.value) return;
        pageNum.value = newPage;
        getUserVideoList();
    };

    // 页面加载时请求数据
    onMounted(() => {
        getUserInfo();
        getUserVideoList();
    });
</script>