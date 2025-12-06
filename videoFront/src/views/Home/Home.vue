<!-- 网站主页组件 -->
<template>
    <div class="home-container">
        <h1 style="text-align: center; margin: 0 0 20px 0; color: #5650a8;">全部视频</h1>

        <!-- 视频列表 -->
        <div style="display: flex; flex-direction: column; gap: 12px; max-width: 1200px; margin: 0 auto;">
            <div v-if="loading" style="
                height: 400px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #999;
                font-size: 14px;
            ">加载中...</div>

            <!-- 循环渲染所有视频 -->
            <div 
                v-else
                v-for="video in currentPageVideos" 
                :key="video.vid"
                style="border: 1px solid #eee; padding: 18px; border-radius: 8px; cursor: pointer; transition: border-color 0.3s;"
                @click="goToPlay(video.vid)"
                @mouseenter="(e) => e.target.style.borderColor = '#5650a8'"
                @mouseleave="(e) => e.target.style.borderColor = '#eee'"
            >
                <h3 style="margin: 0 0 8px 0; color: #333; font-size: 16px; line-height: 1.4; text-align: center;">{{ video.title }}</h3>
                <p style="margin: 0; color: #666; font-size: 14px; line-height: 1.4; text-align: center;">作者：{{ video.author }}</p>
            </div>

            <!-- 无数据占位 -->
            <div v-if="!loading && currentPageVideos.length === 0" style="
                height: 400px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #999;
                font-size: 14px;
            ">暂无视频数据</div>
        </div>

        <!-- 分页控件 -->
        <div v-if="total > 0" style="margin: 30px 0 20px 0; text-align: center; max-width: 1200px; margin: 30px auto 0;">
            <button 
                style="margin: 0 5px; padding: 4px 12px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: #fff; line-height: 1.5;"
                @click="changePage(pageNum - 1)"
                :disabled="pageNum <= 1"
                :style="pageNum <= 1 ? {cursor: 'not-allowed', color: '#999', borderColor: '#eee'} : {}"
            >
                上一页
            </button>
            <span style="margin: 0 10px; font-size: 14px; color: #666;">
                第 {{ pageNum }} 页 / 共 {{ totalPage }} 页（总计 {{ total }} 条）
            </span>
            <button 
                style="margin: 0 5px; padding: 4px 12px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: #fff; line-height: 1.5;"
                @click="changePage(pageNum + 1)"
                :disabled="pageNum >= totalPage"
                :style="pageNum >= totalPage ? {cursor: 'not-allowed', color: '#999', borderColor: '#eee'} : {}"
            >
                下一页
            </button>
        </div>
    </div>
</template>

<!-- 复用视频页面的scoped样式逻辑 -->
<style scoped>
    .home-container {
        position: fixed;
        top: 100px; 
        left: 0;
        right: 0;
        padding: 20px;
        background: #fff;
        min-height: auto;
        max-height: calc(100vh - 120px); /* 留底部间距，不溢出 */
        box-sizing: border-box;
        overflow-y: auto; /* 内容多了滚动，不挤标题 */
        z-index: 1; /* 层级合理，不被覆盖 */
    }
</style>

<script setup>
    
    import request from '../../api/request'
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'

    const router = useRouter()
    // 分页参数（和后端接口参数名完全一致）
    const pageNum = ref(1) // 默认第一页
    const pageSize = ref(8) // 每页8条（可按需调整）
    const keyword = ref("") // 空字符串=查询全部视频

    // 加载状态
    const loading = ref(false)

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
        loading.value = true
        request({
            url: '/videos/list', // 后端接口路径，完全对齐
            method: 'get',
            params: {
                pageNum: pageNum.value,
                pageSize: pageSize.value,
                keyword: keyword.value // 空字符串=查全部
            }
        }).then(res => {
            // 后端返回格式：Result<PageResult> → 
            // res 是 PageResult 实体(因为有响应拦截器处理,原来返回是Result响应类)
            const pageResult = res;
            currentPageVideos.value = pageResult.data; // 后端 PageResult 里的视频列表字段
            total.value = pageResult.total; // 总条数
            totalPage.value = pageResult.totalPage; // 总页数
        }).catch(err => {
            console.error('分页查询全部视频失败：', err)
        }).finally(() => {
            loading.value = false
        })
    }

    // 切换页码（上一页/下一页）
    const changePage = (newPage) => {
        if (newPage < 1 || newPage > totalPage.value) return;  // 数据不支持有下一页则直接返回
        pageNum.value = newPage;
        fetchVideoPage(); // 重新请求新页码数据
    }

    // 跳转视频播放页
    const goToPlay = (vid) => {
        if (vid && vid.trim() !== '') {
            router.push(`/video/${vid}`)
        } else {
            alert('视频vid无效，无法播放')
        }
    }
</script>