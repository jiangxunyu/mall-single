package com.mall.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.mapper.UserMapper;
import com.mall.po.entity.User;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String uri = request.getRequestURI();

            // 白名单直接放行
            if (securityProperties.getIgnoreUrls().contains(uri)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = request.getHeader("token");
            if (token != null && !token.isBlank()) {
                // logout / kickout / token replacement blacklist
                String blacklist = redisTemplate.opsForValue().get("token:blacklist:" + token);
                if (blacklist != null) {
                    writeUnauthorized(response, "登录凭证已失效，请重新登录");
                    return;
                }

                String username = JwtUtil.getUsername(token);

                // 防止重复认证，
                /**
                 * 重复认证问题：如果每次请求都解析 token、查询数据库、构建认证对象，性能会很差。
                 * 解决方案：先检查 SecurityContextHolder 中是否已有认证对象，如果有且用户名匹配，就直接放行，不再重复认证。只有当没有认证对象或用户名不匹配时，才进行 token 解析和数据库查询。这种方式可以大大减少重复
                 * SecurityContextHolder.getContext().getAuthentication() 返回当前线程的认证对象，如果没有认证对象则返回 null。只有当 token 中的用户名与当前认证对象的用户名不匹配时，才进行后续的 token 解析和数据库查询。这种方式可以有效避免重复认证，提高性能。
                 * SecurityContext 不是跨请求持久的，而是每个请求都会创建一个新的 SecurityContext 对象，并在请求结束后销毁。因此，认证对象只会在当前请求的上下文中存在，不会跨请求共享。这也是为什么需要在每个请求中进行认证检查的原因。
                 * 只在当前这一次请求线程里有效。请求结束后线程回收，这个上下文就没了。
                 */
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userMapper.findByUsername(username);
                    if (user == null) {
                        writeUnauthorized(response, "用户不存在");
                        return;
                    }

                    // Redis 校验（核心）
                    String key = "user:token:" + user.getId();
                    String redisToken = redisTemplate.opsForValue().get(key);

                    // token 不一致 -> 被踢下线或主动退出
                    if (redisToken == null || !redisToken.equals(token)) {
                        writeUnauthorized(response, "账号已退出或在其他设备登录");
                        return;
                    }

                    // 查数据库（带权限）
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 构建认证对象（核心）
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 放入上下文（必须）
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 自动续期：同时刷新 Redis 中的 token
                    if (JwtUtil.shouldRefresh(token)) {
                        String newToken = JwtUtil.generateTokenByName(username);
                        redisTemplate.opsForValue().set(key, newToken, 30, TimeUnit.MINUTES);
                        response.setHeader("Authorization", "Bearer " + newToken);
                    }
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 关键：必须写响应并终止
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("""
                    {"code":401,"msg":"token无效或已过期"}
                    """);
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
