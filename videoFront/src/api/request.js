import axios from "axios";

const request = axios.create({
    //后端根地址(后端接口的公共前缀)
    baseURL: "http://localhost:8080",
    timeout: 8000
})

//请求拦截器（用于发起请求时向请求信息添加相关信息）
request.interceptors.request.use(
    (config) => {
        //此处方便后续加token相关逻辑

        //返回
        return config
    },

    //错误处理部分
    (error) => {
        alert("请求发送失败，请检查网络")
        return Promise.reject(error)
    }
)

//响应拦截器（收到后端返回结果后添加相关判断逻辑）
request.interceptors.response.use(
    (response) => {
        //接收返回的结果内容（后端返回Result类）
        const result = response.data;
        
        //判断是否响应正确
        if(result.code != 200){
            console.log(`后端有响应，响应码为${result.code}，报错`)
            return Promise.reject(result.message || "操作失败")
        }
        return result.data
    },

    //错误处理--后端无响应
    (error) => {
        console.log("服务器无响应")
        return Promise.reject(error)
    }
)

export default request