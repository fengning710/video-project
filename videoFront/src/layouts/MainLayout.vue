<!-- src/layouts/MainLayout.vue -->
<template>
    <div class="main-layout">
        <!-- 菜单栏 -->
        <Navbar />
        <!-- 页面内容区域（用 router-view 渲染子页面） -->
        <main class="content">
        <router-view />
        </main>
    </div>
</template>

<script setup>
    // 引入菜单栏组件
    import Navbar from '../components/Nacbar.vue'
</script>

<style scoped>
/* 内容区域留顶部间距（避免被固定菜单覆盖） */
.content {
  margin-top: 90px; /* 与菜单高度一致 */
  padding: 20px;
  min-height: calc(100vh - 90px);
  box-sizing: border-box;
}
</style>