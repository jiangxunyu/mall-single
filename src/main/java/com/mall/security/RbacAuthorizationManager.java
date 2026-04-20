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

    private static final String SUPER_ADMIN = "SUPER_ADMIN";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String url = request.getRequestURI();

        if (securityProperties.getIgnoreUrls().contains(url)) {
            return new AuthorizationDecision(true);
        }

        String method = request.getMethod();
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        boolean isAdmin = authorities.stream().anyMatch(a ->
                SUPER_ADMIN.equals(a.getAuthority()) || ROLE_ADMIN.equals(a.getAuthority()));
        if (isAdmin) {
            return new AuthorizationDecision(true);
        }

        List<String> permissions = permissionService.getPermissions(url, method);
        for (GrantedAuthority authority : authorities) {
            if (permissions.contains(authority.getAuthority())) {
                return new AuthorizationDecision(true);
            }
        }

        return new AuthorizationDecision(false);
    }
}
