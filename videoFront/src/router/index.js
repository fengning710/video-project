import {createRouter, createWebHistory } from 'vue-router'
//引入组件
import Login from '../views/Login/Login.vue'

//定义路由规则（路径 -> 组件）
const routes = [
    {
        path: '/login',
        component: Login
    }
]

//创造路由实例
const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router