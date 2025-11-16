package com.example.videoapp.util;

import com.example.videoapp.exception.BusinessException;
import com.example.videoapp.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头取token
        String token = request.getHeader("Authorization");
        // 2. 处理Bearer前缀（和前端对应）
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 3. 验证token有效性
        if(token == null){
            throw new BusinessException(ErrorCode.USER_NOT_LOGIN);
        }
        if (!JwtUtils.isTokenValid(token)) {
            // 无效返回405
            throw new BusinessException(ErrorCode.USER_WRONG_TOKEN);
        }
        return true;
    }
}

