package com.euler.housekeepingservice.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 安全上下文工具类
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户的 ID
     * (由于我们在 JwtAuthenticationFilter 中将 userId 存入了 Principal，这里直接取出强转即可)
     */
    public static Long getUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Long) {
                return (Long) authentication.getPrincipal();
            }
        } catch (Exception e) {
            throw new BizException(401, "获取当前登录用户信息失败，请重新登录");
        }
        throw new BizException(401, "未授权的访问");
    }
}