<template>
    <div style="padding: 20px; max-width: 1200px; margin: 0 auto;">
        <h1 style="text-align: center; color: #5650a8; margin: 30px 0;">全部视频</h1>

        <!-- 视频列表：渲染当前页视频 -->
        <div style="display: flex; flex-direction: column; gap: 12px;">
            <div 
                v-for="video in currentPageVideos" 
                :key="video.vid"
                style="border: 1px solid #eee; padding: 18px; border-radius: 8px; cursor: pointer; transition: border-color 0.3s;"
                @click="goToPlay(video.vid)"
                hover:border-color="#5650a8"
            >
                <h3 style="margin: 0 0 8px 0; color: #333;">{{ video.title }}</h3>
                <p style="margin: 0; color: #666; font-size: 14px;">作者：{{ video.author }}</p>
            </div>
        </div>

        <!-- 分页控件（适配后端分页逻辑） -->
        <div style="margin: 30px 0; text-align: center;">
            <button 
                style="margin: 0 5px; padding: 4px 12px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer;"
                @click="changePage(pageNum - 1)"
                :disabled="pageNum <= 1"
            >
                上一页
            </button>
            <span style="margin: 0 10px;">
                第 {{ pageNum }} 页 / 共 {{ totalPage }} 页（总计 {{ total }} 条）
            </span>
            <button 
                style="margin: 0 5px; padding: 4px 12px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer;"
                @click="changePage(pageNum + 1)"
                :disabled="pageNum >= totalPage"
            >
                下一页
            </button>
        </div>
    </div>
</template>

<script setup>
    // 关键调整：导入路径适配你的项目目录（../../api/request）
    import request from '../../api/request'
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'

    const router = useRouter()
    // 分页参数（和后端接口参数名完全一致）
    const pageNum = ref(1) // 默认第一页
    const pageSize = ref(4) // 每页4条（可按需调整）
    const keyword = ref("") // 空字符串=查询全部视频

    // 接收后端返回的分页数据
    const currentPageVideos = ref([]) // 当前页视频列表（data）
    const total = ref(0) // 总条数
    const totalPage = ref(1) // 总页数

    // 页面加载时请求第一页
    onMounted(() => {
        fetchVideoPage()
    })

    // 核心：调用后端 /videos/list 接口（分页查询全部视频）
    const fetchVideoPage = () => {
        request({
            url: '/videos/list', // 后端接口路径，完全对齐
            method: 'get',
            params: {
                pageNum: pageNum.value,
                pageSize: pageSize.value,
                keyword: keyword.value // 空字符串=查全部
            }
        }).then(res => {
            // 后端返回格式：Result<PageResult> → res.data 是 PageResult 实体
            const pageResult = res;
            currentPageVideos.value = pageResult.data; // 后端 PageResult 里的视频列表字段
            total.value = pageResult.total; // 总条数
            totalPage.value = pageResult.totalPage; // 总页数
        }).catch(err => {
            console.error('分页查询全部视频失败：', err)
        })
    }

    // 切换页码（上一页/下一页）
    const changePage = (newPage) => {
        if (newPage < 1 || newPage > totalPage.value) return;
        pageNum.value = newPage;
        fetchVideoPage(); // 重新请求新页码数据
    }

    // 跳转视频播放页
    const goToPlay = (vid) => {
        if (vid && vid.trim() !== '') {
            router.push(`/video/${vid}`)
        } else {
            alert('视频VID无效，无法播放')
        }
    }
</script>