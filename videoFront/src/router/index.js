import {createRouter, createWebHistory } from 'vue-router'
//引入页面组件
import Login from '../views/Login/Login.vue'
import Home from '../views/Home/Home.vue'
import Register from '../views/Register/Register.vue'
import Video from '../views/Video/Video.vue'

//引入通用组件
import MainLayout from '@/layouts/MainLayout.vue'

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
        path: '/register',
        component: Register,
        meta:{
            requiresAuth:false  //不需要登录验证
        }
    },
    {
        path: '/profile',  // 个人主页 留待拓展  记得设登录验证
        redirect: '/' 
    },

    {
        path: '/',
        component: MainLayout,
        children:[
            {
                path: '/',
                component: Home,
                meta:{
                    requiresAuth:false  //不需要登录验证
                }
            },
            {
                path: 'video',       // 绝对前面不能加/
                redirect: '/video/VMfengjin1'
            },
            {
                path: 'video/', 
                redirect: '/video/VMfengjin1'
            },
            {
                path: 'video/:videoId',
                component: Video,
                meta:{
                    requiresAuth:false  //不需要登录验证
                }
            }
        ]
    }
]

//创造路由实例
const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router