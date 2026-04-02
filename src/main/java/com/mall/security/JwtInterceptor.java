package com.mall.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        if(request.getRequestURI().contains("/user/login")){
            return true;
        }
        if (request.getRequestURI().contains("/user/register")){
            return true;
        }

        String token = request.getHeader("token");

        if(token == null){
            throw new RuntimeException("未登录");
        }

        jwtUtil.parse(token);

        return true;
    }
}