package com.example.videoapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 标记为配置类，Spring会自动加载该类的配置
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // 重写WebMvcConfigurer的跨域配置方法
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                // 1. 允许哪些接口跨域？"/**"表示所有接口（包括子路径，如/learn/testFront）
                .addMapping("/**")

                // 2. 允许哪些前端源访问？填写你的前端地址（必须带http/https和端口）
                // 例如：前端Vue项目地址是http://localhost:5173，就填这个
                .allowedOrigins("http://localhost:5173"
                        ,"null" //此处为静态页面跨域使用
                        ,"http://127.0.0.1:5500"
                        //,"http://192.168.81.48:5173"  //此处为局域网设置，记得为对应的域名
                )

                // 3. 允许哪些HTTP请求方法？GET（查询）、POST（提交）、PUT（修改）、DELETE（删除）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // 4. 允许哪些请求头？"*"表示所有请求头（如Content-Type、Authorization等）
                .allowedHeaders("*")

                // 暴露分段响应头（前端需要读取）
                .exposedHeaders("Accept-Ranges", "Content-Range")

                // 5. 是否允许前端携带Cookie等凭证？（如登录状态，开发环境建议开启）
                // JWT Token 认证用不到（cookie区没存东西）
                .allowCredentials(false)

                // 6. 预检请求的缓存时间（单位：秒）。浏览器会先发送预检请求确认是否允许跨域，缓存后减少重复请求
                .maxAge(3600);
    }
}
