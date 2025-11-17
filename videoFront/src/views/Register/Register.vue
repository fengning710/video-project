<template>
    <div class="Login-container">
        <div class="Login-card">
            <h2>注册</h2>
            
            <div class="input-item">
                <label>用户名：</label>
                <input type="text" v-model="form.userName" placeholder="请输入用户名" class="login-input"/>
            </div>
            
            <div class="input-item">
                <label>密码：</label>
                <input type="password" v-model="form.password" placeholder="请输入密码" class="login-input"/>
            </div>
            <div class="input-item">
                <label>确认密码：</label>
                <input type="password" v-model="form.confirmPassword" placeholder="请确认密码" class="login-input"/>
            </div>
            
            <!-- 错误提示 -->
            <p class="" v-if="loading"> {{ loadingMsg }} </p>
            <p class="error-message" v-else-if="errorMsg">{{ errorMsg }}</p>
            <p class="success-message" v-else-if="successMsg">{{ successMsg }}</p>
            <p v-else></p>

            <button class="Login-btn" @click="toRegister">点击注册</button>
            <br/>
            <p id="Login-nologin">已有账号？去<router-link to="/login">登录</router-link></p>
        </div>
    </div>
</template>

<script setup>
    // 1. 导入 reactive（用于创建响应式对象）
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { userRegister } from '@/api/modules/userApi';

// 2. 定义 form 响应式对象，包含模板中用到的属性
const form = reactive({
  userName: '', // 用户名
  password: '', // 密码
  confirmPassword: '' // 确认密码
});
const loading = ref(false);
const errorMsg = ref("");
const successMsg = ref("");
const loadingMsg = ref("注册中，请稍后...");

// 3. 新增路由，用来跳转页面
const router = useRouter();

// 4. 按钮点击函数（async 异步操作，配合 await 使用）
const toRegister = async() => {
    if (!form.userName.trim()) {
        errorMsg.value = "用户名不能为空";
        successMsg.value = null;
        return;
    }
    if (!form.password.trim() || !form.confirmPassword.trim()) {
        errorMsg.value = "密码不能为空";
        successMsg.value = null;
        return;
    }
    if (form.password !== form.confirmPassword) {
        errorMsg.value = "两次输入的密码不一致";
        successMsg.value = null;
        return;
    }

    try{
        // 开始注册流程，渲染字样
        loading.value = true;
        errorMsg.value = null;
        
        const response = await userRegister({
            userName: form.userName,
            userPassword: form.password
        })
        successMsg.value = "注册成功！即将跳转到登录页面...";
        errorMsg.value = null;

        // 注册成功后，延时跳转到登录页面
        setTimeout(() => {
            router.push('/login')
        }, 2000);
        
    }catch (error) {
        errorMsg.value = error || "注册失败，请稍后重试";
        successMsg.value = null;
    }finally {
        loading.value = false;
    }
}

</script>

<style scoped>
    .Login-btn{
        font-family: "汉仪文黑85W";
        color: #fff;
        background-color: aqua;
    }
    .input-item{
        margin: 20px auto;
    }
    .Login-card {
        /* 关键：设置固定宽度（或最大宽度，避免过小屏幕变形） */
        width: 500px; /* 固定宽度 */
        /* 或用 max-width: 90%; 限制最大宽度，适配小屏幕 */
        max-width: 90%;

        /* 高度可选：如果内容不多，可不用固定，让内容撑开 */
        /* height: 300px; */

        /* 其他样式：保持卡片独立外观 */
        padding: 30px;
        background: rgba(255, 255, 255, 0.8); /* 卡片白色背景（半透明） */
        border-radius: 8px; /* 圆角 */
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 阴影，突出卡片 */
    }
    .Login-container{
        width: 100vw; /* 宽度占满整个视口 */
        height: 100vh; /* 高度占满整个视口 */
        background-image: url('@/assets/images/login-image_resized.png');
        background-size: cover; /* 图片铺满容器 */
        background-position: center;
        background-repeat: no-repeat;
        background-position: left top;

        /* 关键：补全flex居中属性 */
        display: flex;
        justify-content: center; /* 水平居中 */
        align-items: center; /* 垂直居中 */
        margin: 0 0; /* 避免容器自身有默认margin */
        padding: 0 0; /* 避免容器自身有默认padding */
    }

    /* 输入框专属样式，仅登录页生效 */
.login-input {
    width: 240px; /* 适配卡片宽度 */
    height: 36px;
    margin-left: 8px; /* 与标签隔开一点距离 */
    padding: 0 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
}
.login-input:focus {
    outline: none;
    border-color: aqua; /* 与按钮颜色呼应 */
    box-shadow: 0 0 0 2px rgba(0, 255, 255, 0.2); /* 聚焦高亮 */
}

.Login-nologin{
    margin: 0 0 0 300px;
}
</style>
