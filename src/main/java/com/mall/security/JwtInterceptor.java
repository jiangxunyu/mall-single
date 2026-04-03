package com.mall.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器（简化版）
 * 仅验证 token 是否存在和有效，不处理权限
 * 加权限验证的时候不建议使用 HandlerInterceptor，推荐使用 Spring Security 的 Filter（如 JwtAuthenticationFilter）来处理认证和授权逻辑
 * 因为 HandlerInterceptor 主要用于处理请求前后的逻辑，而 Filter 更适合处理安全相关的逻辑，如认证和授权，且能更好地与 Spring Security 集成
 * 这里的 JwtInterceptor 主要用于演示如何在 HandlerInterceptor 中验证 JWT，实际项目中建议使用 Filter 来处理 JWT 认证和授权逻辑
 */
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

        jwtUtil.parseToken(token);

        return true;
    }
}