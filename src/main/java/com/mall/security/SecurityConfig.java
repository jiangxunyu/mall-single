package com.mall.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 启动时调用一次：整个配置类在Spring容器启动时被初始化，filterChain等方法被调用一次，用于构建安全规则。
 * 请求时持续生效：构建好的安全过滤器链会对每一个进入应用的HTTP请求生效。具体来说：
 * 每次请求都会经过JwtAuthenticationFilter来检查JWT Token。
 * 每次请求都会根据authorizeHttpRequests中定义的规则进行路径匹配和权限判断。
 * 所以，简单地说，这个类在启动时“设定好规则”，而这些规则在应用的整个生命周期内“持续执行”
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;
    @Autowired
    private RbacAuthorizationManager rbac;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/user/login",
                                "/user/register",
                                "/product/{id}",
                                "/seckill/{productId}",
                                "/product/search",
                                "/order/create",
                                "/order/createByCart",
                                "/es/searchByName",
                                "/es/search",
                                "/cart/**"
                        ).permitAll()
                        //除了 /login /register 之外
                        .anyRequest().access(rbac)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception -> exception
                                // 注册认证失败处理器
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                // 注册授权失败处理器
                                .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}