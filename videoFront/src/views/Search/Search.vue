<!-- 搜索页组件（搜索栏输入信息进行视频模糊查询后会跳到该页） -->
<template>
    <div style="padding: 20px; max-width: 1200px; margin: 0 auto;">
        <!-- 搜索页内的搜索栏（同时菜单栏的搜索栏被隐藏） -->
        <div style="margin-bottom: 20px; text-align: center;">
            <input 
                type="text"
                v-model="keyword"
                placeholder="输入关键词（标题）搜索视频..."
                style="padding: 6px 12px; width: 300px; border: 1px solid #ddd; border-radius: 4px;"
            />
            <button 
                @click="search"
                style="margin-left: 10px; padding: 6px 16px; border: none; border-radius: 4px; background-color: #3498db; color: white; cursor: pointer;"
            >
                查询
            </button>
        </div>

        <h2 style="color: #00b3ff; margin-bottom: 20px;">
            搜索结果：{{ keyword }}（总计 {{ total }} 条）
        </h2>

        <div v-if="total === 0" style="text-align: center; margin: 50px 0; color: #999;">
            未找到相关视频，请更换关键词试试～
        </div>

        <div v-else style="display: flex; flex-direction: column; gap: 12px;">
            <div 
                v-for="video in currentPageVideos" 
                :key="video.vid"
                style="border: 1px solid #eee; padding: 18px; border-radius: 8px; cursor: pointer; transition: border-color 0.3s;"
                @click="goToPlay(video.vid)"
                hover:border-color="#5650a8"
            >
                <h3 style="margin: 0 0 8px 0; color: #333;">{{ video.title }}</h3>
                <p style="margin: 0; color: #666; font-size: 14px;">作者：{{ video.author || '未知作者' }}</p>
            </div>
        </div>

        <!-- 分页部分按钮 -->
        <div v-if="total > 0" style="margin: 30px 0; text-align: center;">
            <button 
                style="margin: 0 5px; padding: 4px 12px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer;"
                @click="changePage(pageNum - 1)"
                :disabled="pageNum <= 1"
            >
                上一页
            </button>
            <span style="margin: 0 10px;">
                第 {{ pageNum }} 页 / 共 {{ totalPage }} 页
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
    import request from '../../api/request'
    import { ref, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'

    const router = useRouter()
    const route = useRoute()

    const pageNum = ref(1)
    const pageSize = ref(8)
    const keyword = ref("") // 绑定搜索输入框

    const currentPageVideos = ref([])
    const total = ref(0)
    const totalPage = ref(1)

    // 页面加载时：从路由参数拿初始关键词（比如从主页跳转过来）
    onMounted(() => {
        const initKeyword = route.query.keyword || "";
        keyword.value = initKeyword; // 输入框初始值=路由参数关键词
        fetchVideoPage(); // 加载初始搜索结果
    })

    // 搜索按钮点击事件 -> 更新参数+重新请求
    const search = () => {
        pageNum.value = 1; // 重置为第1页（避免页码错乱）
        fetchVideoPage(); // 传递新的keyword，重新请求接口
    }

    // 分页请求接口（不变，自动传递最新的keyword）
    const fetchVideoPage = () => {
        request({
            url: '/videos/list',
            method: 'get',
            params: {
                pageNum: pageNum.value,
                pageSize: pageSize.value,
                keyword: keyword.value // 传递当前输入的keyword
            }
        }).then(res => {
            const pageResult = res;
            currentPageVideos.value = pageResult.data;
            total.value = pageResult.total;
            totalPage.value = pageResult.totalPage;
        }).catch(err => {
            console.error('分页搜索视频失败：', err)
        })
    }

    // 切换页码
    const changePage = (newPage) => {
        if (newPage < 1 || newPage > totalPage.value) return;
        pageNum.value = newPage;
        fetchVideoPage();
    }

    // 跳转播放
    const goToPlay = (vid) => {
        if (vid && vid.trim() !== '') {
            router.push(`/video/${vid}`)
        } else {
            alert('视频vid无效，无法播放')
        }
    }
</script>