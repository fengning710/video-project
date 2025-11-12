
<template>
    <div class="Login-container">
        <div class="Login-card">
            <h2>登录</h2>
            
            <div class="input-item">
                用户名：
                <input type="text" v-model="form.userName" placeholder="请输入用户名" class="login-input"/>
            </div>
            
            <div class="input-item">
                密码：
                <input type="password" v-model="form.password" placeholder="请输入密码" class="login-input"/>
            </div>
            <!-- 错误提示 -->
            <p class="" v-if="loading">{{ loadingMsg }}</p>
            <p class="error-message" v-else-if="errorMsg">{{ errorMsg }}</p>
            <p class="success-message" v-else-if="successMsg">{{ successMsg }}</p>
            <p v-else></p>
            
            <button class="Login-btn" @click="toLogin">点击登录</button>
            <br/>
            <p id="Login-nologin">没有账号？去<router-link to="">注册</router-link></p>
        </div>
    </div>
</template>

<script setup>
    // 导入 Vue 的 reactive 函数（用于创建响应式对象）
    import axios from 'axios'
    import { reactive,ref } from 'vue'
    // import { useRouter } from 'vue-router'
    import { userLogin } from '@/api/modules/userApi'
    // import { useUserStore } from '@/store/modules/user'

    // 定义 form 和其他响应式对象
    const form = reactive({
        userName: '', // 用户名初始值为空字符串
        password: ''  // 密码初始值为空字符串
    })
    const loading = ref(false)
    const errorMsg = ref("")
    const successMsg = ref("")
    const loadingMsg = ref("登录中，请稍后...")

    //按钮点击函数（async异步操作，配合await使用）
    const toLogin = async() =>{
        errorMsg.value = null
        if(!form.userName.trim()){
            errorMsg.value = "用户名不能为空"
            successMsg.value = null
            return
        }
        if(!form.password.trim()){
            errorMsg.value = "密码不能为空"
            successMsg.value = null
            return
        }

        try{
            //开始登录流程，渲染字样
            loading.value = true

            //写aixos(注意变量名，随时更改)
            // const response = await axios.post("http://localhost:8080/login",
            //     {
            //         userName: form.userName,
            //         userPassword:form.password
            //     }
            // )
            // const result = response.data
            const result = await userLogin({
                userName: form.userName,
                userPassword:form.password
            })
            successMsg.value = `欢迎登录，${result.userName}`

        }catch(error){
            errorMsg.value = error
            successMsg.value = null
        }finally{
            loading.value = false
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
