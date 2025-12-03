<template>
    <nav class="navbar">
        <!-- 左侧 Logo -->
        <router-link to="/" class="navbar-logo">视频小屋</router-link>

        <!-- 中间：导航项 + 搜索栏 -->
        <div class="navbar-middle">
            <div class="navbar-nav">
                <router-link 
                    to="/" 
                    class="nav-link"
                    :class="{ active: $route.path === '/' }"
                >
                    首页
                </router-link>

                <router-link 
                    v-if="isLogin"
                    to="/profile" 
                    class="nav-link"
                    :class="{ active: $route.path === '/profile' }"
                >
                    个人中心
                </router-link>
            </div>

            <!-- 核心：固定占位容器（宽度=搜索栏宽度，始终存在） -->
            <div class="search-placeholder">
                <!-- 搜索栏：隐藏时仅自身消失，占位容器保留 -->
                <div class="search-bar" v-if="!isSearchPage">
                    <input
                        type="text"
                        v-model="searchInput"
                        placeholder="搜索视频标题..."
                        class="search-input"
                        @keyup.enter="handleSearch"
                    />
                    <button @click="handleSearch" class="search-btn">搜索</button>
                </div>
            </div>
        </div>

        <!-- 右侧用户区域 -->
        <div class="navbar-user">
            <template v-if="isLogin">
                <span class="user-name">欢迎，{{ userInfo?.userName || '用户' }}</span>
                <!-- 新增：上传视频按钮（退出登录左边，样式匹配视频小屋） -->
                <router-link to="/videoUpload" class="upload-btn">上传视频</router-link>
                <button @click="handleLogout" class="logout-btn">退出登录</button>
            </template>
            <template v-else>
                <router-link to="/login" class="login-link">登录</router-link>
                <router-link to="/register" class="register-link">注册</router-link>
                <!-- 新增：未登录状态也显示上传按钮 -->
                <router-link to="/videoUpload" class="upload-btn">上传视频</router-link>
            </template>
        </div>
    </nav>
</template>

<script setup>
    import { useRouter, useRoute } from 'vue-router'
    import { ref, onMounted, computed } from 'vue'

    const router = useRouter();
    const route = useRoute();  // 获取当前路由信息
    const userInfo = ref(null);
    const isLogin = ref(false);

    // 搜索页判断（路由路径为 /search 时隐藏搜索栏）
    const isSearchPage = computed(() => {
        return route.path === '/search'
    });

    // 搜索相关
    const searchInput = ref('') // 绑定搜索输入框

    // 搜索逻辑（跳转到搜索结果页，携带搜索关键词）
    const handleSearch = () => {
        if (searchInput.value.trim()) {
            // 跳转到 /search 页面
            router.push({
                path: '/search', // 搜索结果页路径是 /search
                query: { keyword: searchInput.value.trim() } // URL 携带关键词：/search?keyword=xxx
            })
            searchInput.value = '' // 清空输入框
        } else {
            alert('请输入搜索关键词～')
        }
    }

    // 页面加载读取用户信息
    onMounted(() => {
        const storedToken = localStorage.getItem('token')
        const storedUser = localStorage.getItem('user')
        isLogin.value = !!storedToken
        if (storedUser) {
            try {
                userInfo.value = JSON.parse(storedUser)
            } catch (err) {
                console.log('用户数据解析失败', err)
                userInfo.value = null
            }
        }
    })

    // 退出登录
    const handleLogout = () => {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        userInfo.value = null
        isLogin.value = false
        router.push('/login')
    }
</script>

<style scoped>
    /* 原有样式不变，新增 .navbar-middle、.search-bar 相关样式 */
    .navbar {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        height: 80px; /* 加高菜单高度（容纳搜索栏，默认60px → 100px） */
        background-color: #ffffff; /* 自定义背景色 */
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        display: flex;
        align-items: center; /* 垂直居中 */
        justify-content: space-between; /* 水平分布 */
        padding: 0 30px;
        z-index: 999;
    }

    /* 新增：中间区域（导航项+搜索栏）布局 */
    .navbar-middle {
        display: flex;
        flex-direction: row; /* 导航项，搜索栏平行 */
        gap: 100px; /* 导航项和搜索栏的间距 */
        align-items: center; /* 中间区域整体居中 */
    }

    /* 原有导航项样式（可微调） */
    .navbar-nav {
        display: flex;
        gap: 40px; /* 导航项间距 */
    }

    /* 核心：搜索固定占位容器（宽度=搜索栏宽度，始终存在） */
    .search-placeholder {
        width: 350px; /* 和搜索栏总宽度一致（输入框+按钮） */
        display: flex; /* 保持和搜索栏相同的布局上下文 */
    }

    /* 新增：搜索栏样式 */
    .search-bar {
        display: flex;
        gap: 8px; /* 输入框和按钮间距 */
        width: 100%; /* 占满占位容器宽度 */
    }

    .search-input {
        flex: 1; /* 输入框占满剩余宽度 */
        padding: 8px 12px;
        border: solid 1px #00b3ff;
        border-radius: 6px;
        font-size: 15px;
        outline: none; /* 去掉输入框默认边框 */
    }

    .search-btn {
        background-color: #3498db; /* 搜索按钮颜色 */
        color: #fff;
        border: none;
        padding: 8px 16px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 15px;
    }

    .search-btn:hover {
        background-color: #2980b9; /* 按钮hover加深色 */
    }

    /* 原有其他样式（Logo、导航项、用户区）不变，按需修改颜色即可 */
    .navbar-logo {
        color: #1890ff;
        font-size: 22px;
        font-weight: 600;
        text-decoration: none;
    }

    .nav-link {
        color: #333;
        font-size: 17px;
        padding: 8px 0;
        position: relative;
        text-decoration: none;
    }

    .nav-link.active {
        color: #333;
    }

    .nav-link.active::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        height: 2px;
        background-color: #1890ff;
    }

    /* 其余样式（.navbar-user、.user-name、.logout-btn 等）不变，按需修改颜色 */
    .navbar-user {
        display: flex;
        align-items: center;
        gap: 20px;
    }

    .user-name {
        color: #666;
        font-size: 15px;
    }

    .logout-btn {
        background-color: #fb7299;
        color: #fff;
        border: none;
        padding: 7px 14px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 15px;
    }

    .logout-btn:hover {
        background-color: #de6587;
    }

    .login-link, .register-link {
        color: #1890ff;
        text-decoration: none;
        font-size: 15px;
    }

    .register-link {
        border: 1px solid #3498db;
        padding: 4px 12px;
        border-radius: 4px;
    }

    /* 新增：上传视频按钮样式（和视频小屋颜色一致，白字） */
    .upload-btn {
        background-color: #1890ff; /* 匹配 navbar-logo 的颜色 */
        color: #fff; /* 白字 */
        border: none;
        padding: 7px 14px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 15px;
        text-decoration: none;
    }

    .upload-btn:hover {
        background-color: #096dd9; /* hover 加深色，和其他按钮风格一致 */
        color: #fff; /* 保持白字 */
    }
</style>