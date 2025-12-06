import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'
// 引入页面组件
import Login from '../views/Login/Login.vue'
import Home from '../views/Home/Home.vue'
import Register from '../views/Register/Register.vue'
import Video from '../views/Video/Video.vue'
import Search from '../views/Search/Search.vue' // 导入搜索页组件
import Profile from '../views/Profile/Profile.vue' // 导入个人主页组件
import VideoUpload from '../views/VideoUpload/VideoUpload.vue'

// 引入通用组件
import MainLayout from '@/layouts/MainLayout.vue'

// 定义路由规则（路径 -> 组件）
const routes = [
    {
        path: '/login',
        component: Login,
        meta: {
            requiresAuth: false  // 不需要登录验证
        }
    },
    {
        path: '/register',
        component: Register,
        meta: {
            requiresAuth: false  // 不需要登录验证
        }
    },
    

    {
        path: '/',
        component: MainLayout,
        children: [
            {
                path: '/',
                component: Home,
                meta: {
                    requiresAuth: false  // 不需要登录验证
                }
            },
            // 个人主页路由
            {
                path: 'profile',  // 个人主页  设登录验证
                component: Profile,
                meta:{
                    requiresAuth: true // 需要登录验证
                }
            },
            // 视频上传页路由
            {
                path: 'videoUpload',
                component: VideoUpload,
                meta:{
                    requiresAuth: true // 需要登录
                }
            },
            // 搜索页路由（放在MainLayout子路由，复用顶部搜索栏和布局）
            {
                path: 'search',  // 遵循原有规则，绝对路径前不加/
                component: Search,
                meta: {
                    requiresAuth: false  // 不需要登录验证
                }
            },
            {
                path: 'video',       // 绝对前面不能加/
                redirect: 'video/VMfengjin1'
            },
            // {  // vue router 会自动处理后面的 “/”
            //     path: 'video/', 
            //     redirect: '/video/VMfengjin1'
            // },
            {
                path: 'video/:videoId',
                component: Video,
                meta: {
                    requiresAuth: false  // 不需要登录验证
                }
            }
        ]
    }
]

// 创造路由实例
const router = createRouter({
    history: createWebHashHistory(),  // 该方法适配本地文件打开，平时可用去掉 Hash 的方法
    routes
});

// 加全局前置路由守卫（使用于判断用户是否登录）
router.beforeEach((to,from,next) =>{
    /**
     * 获取登录状态(2个感叹号是因为：1个判断有无token并转 boolean 值，另一个转回原来逻辑)
     *  例：
     *      如果有 token，取到token后getItem函数会返回字符串，经过1个!判断后转换为false，
     *      此时使用另一个! 判断就可取得true，即获得token的逻辑，并赋给常量
     *   因为js不能直接用字符串判断true/false
     */ 
    const isLogin = !!localStorage.getItem('token');

    // 判断目标路由是否要登录
    if(to.meta.requiresAuth){
        // 需要登录的页面路由
        if(isLogin){
            next(); // 放行
        }else{
            next('/login'); // 跳登录页
        }
    }else{
        // 不要登录：放行
        next();
    }

});

export default router