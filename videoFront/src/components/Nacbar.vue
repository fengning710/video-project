<!-- src/components/Navbar.vue -->
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

      <!-- 搜索栏 -->
      <div class="search-bar">
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

    <!-- 右侧用户区域 -->
    <div class="navbar-user">
      <template v-if="isLogin">
        <span class="user-name">欢迎，{{ userInfo?.userName || '用户' }}</span>
        <button @click="handleLogout" class="logout-btn">退出登录</button>
      </template>
      <template v-else>
        <router-link to="/login" class="login-link">登录</router-link>
        <router-link to="/register" class="register-link">注册</router-link>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'

const router = useRouter()
const userInfo = ref(null)
const isLogin = ref(false)

// 新增：搜索相关
const searchInput = ref('') // 绑定搜索输入框

// 搜索逻辑（新手基础版：跳转到搜索结果页，携带搜索关键词）
const handleSearch = () => {
  if (searchInput.value.trim()) {
    // 跳转到 /search 页面（如果没有搜索结果页，可先跳首页并打印关键词）
    // 后续有时间了，再创建 SearchResult.vue 页面，替换下面的路径
    router.push({
      path: '/search', // 假设搜索结果页路径是 /search
      query: { keyword: searchInput.value.trim() } // URL 携带关键词：/search?keyword=xxx
    })
    searchInput.value = '' // 清空输入框
  } else {
    alert('请输入搜索关键词～')
  }
}

// 原有：页面加载读取用户信息
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

// 原有：退出登录
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

/* 新增：搜索栏样式 */
.search-bar {
  display: flex;
  gap: 8px; /* 输入框和按钮间距 */
  width: 350px; /* 搜索栏总宽度 */
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
</style>