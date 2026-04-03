package com.mall.security;

import com.mall.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@Component
public class RbacAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Autowired
    private PermissionService permissionService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext context) {

        HttpServletRequest request = context.getRequest();

        String url = request.getRequestURI();
        String method = request.getMethod();

        Authentication auth = authentication.get();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // 当前用户权限
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        // 数据库权限（可缓存）
        List<String> permissions = permissionService.getPermissions(url, method);

        for (GrantedAuthority authority : authorities) {
            if (permissions.contains(authority.getAuthority())) {
                return new AuthorizationDecision(true);
            }
        }

        return new AuthorizationDecision(false);
    }

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationManager.super.verify(authentication, object);
    }
}