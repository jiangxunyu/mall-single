package com.mall.config;

import com.mall.po.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        Result result = new Result();
        result.setCode(403);
        result.setMsg("权限不足");
        result.setData(null);
        return result;
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result handleAuthentication(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        Result result = new Result();
        result.setCode(401);
        result.setMsg("未登录或登录已过期");
        result.setData(null);
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Result handle(Exception e) {
        log.error("系统异常", e);
        return Result.error(e.getMessage());
    }
}
