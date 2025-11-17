import axios from "axios";
import router from "../router";

const request = axios.create({
    //后端根地址(后端接口的公共前缀)
    baseURL: "http://localhost:8080",
    //baseURL: "http://192.168.1.48:8080", //测试地址(记得换对应IP 控制台ipconfig查询)
    timeout: 8000
});

//请求拦截器（用于发起请求时向请求信息添加相关信息）
request.interceptors.request.use(
    (config) => {
        //此处方便后续加token相关逻辑
        const token = localStorage.getItem("token");
        if(token){
            //添加请求头信息
            config.headers.Authorization = `Bearer ${token}`;
        }
        //返回
        return config;
    },

    //错误处理部分
    (error) => {
        alert("请求发送失败，请检查网络");
        return Promise.reject(error);
    }
)

//响应拦截器（收到后端返回结果后添加相关判断逻辑）
request.interceptors.response.use(
    (response) => {
        //接收返回的结果内容（后端返回Result类）
        const result = response.data;
        
        //判断是否响应正确
        if(result.code == 200){
            return result.data;
        }

        //其他有响应但不是200的情况

        if(result.code == 403 || result.code == 405){  //未登录或登录过期
            console.log(result.message);
            localStorage.clear();
            //跳转登录页面
            router.push(`/login?redirect=${router.currentRoute.value.fullPath}`);
            return Promise.reject(result.message || "请重新登录");
        }else{
            console.log(`后端有响应，响应码为${result.code}，报错`);
            return Promise.reject(result.message || "操作失败");
        }
    },

    //错误处理--后端无响应
    (error) => {
    // 只处理 HTTP 非200（服务器层面的错误，和业务无关）
    let errorMsg = '服务器异常，请稍后再试';
    if (error.response) {
      // 根据HTTP状态码给出精准提示（贴合服务器问题的定位）
        switch (error.response.status) {
            case 404:
                errorMsg = '请求路径不存在（服务器提示：404）';
                break;
            case 500:
                errorMsg = '服务器内部错误（服务器提示：500）';
                break;
            case 400:
                errorMsg = '请求参数格式错误（服务器提示：400）';
                break;
            case 405:
                errorMsg = '请求方式错误（比如用GET调用POST接口，服务器提示：405）';
                break;
        }
    }
    // 抛出服务器错误，让前端捕获提示
    return Promise.reject(errorMsg);
  }
)

export default request