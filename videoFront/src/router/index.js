import {createRouter, createWebHistory } from 'vue-router'
//引入组件
import Login from '../views/Login/Login.vue'
import Home from '../views/Home/Home.vue'

//定义路由规则（路径 -> 组件）
const routes = [
    {
        path: '/login',
        component: Login,
        meta:{
            requiresAuth:false  //不需要登录验证
        }
    },
    {
        path: '/',
        component: Home,
        meta:{
            requiresAuth:false  //不需要登录验证
        }
    }
]

//创造路由实例
const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router