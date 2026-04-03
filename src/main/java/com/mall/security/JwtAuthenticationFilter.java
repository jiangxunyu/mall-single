package com.mall.security;

import com.mall.entity.User;
import com.mall.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {

            token = token.substring(7);

            String username = JwtUtil.getUsername(token);

            // 防止重复认证
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userMapper.findByUsername(username);

                // 🔥 Redis校验（核心）
                String key = "user:token:" + user.getId();
                String redisToken = redisTemplate.opsForValue().get(key);

                // ❌ token不一致 → 被踢下线
                if (redisToken == null || !redisToken.equals(token)) {
                    response.setStatus(401);
                    response.getWriter().write("账号已在其他设备登录");
                    return;
                }

                // 查数据库（带权限）
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 构建认证对象（核心🔥）
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 放入上下文（必须🔥）
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 🔥🔥🔥 自动续期核心逻辑（顺带刷新Redis）
                if (JwtUtil.shouldRefresh(token)) {
                    String newToken = JwtUtil.generateTokenByName(username);
                    redisTemplate.opsForValue().set(key, newToken, 30, TimeUnit.MINUTES);
                    response.setHeader("Authorization", "Bearer " + newToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}