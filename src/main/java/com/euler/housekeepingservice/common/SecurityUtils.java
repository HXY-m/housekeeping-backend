package com.euler.housekeepingservice.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

public class SecurityUtils {
    public static Long getUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Long) {
                return (Long) authentication.getPrincipal();
            }
        } catch (Exception e) {
            throw new BizException(401, "鑾峰彇褰撳墠鐧诲綍鐢ㄦ埛淇℃伅澶辫触锛岃閲嶆柊鐧诲綍");
        }
        throw new BizException(401, "鏈巿鏉冪殑璁块棶");
    }

    public static Integer getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object details = authentication == null ? null : authentication.getDetails();
        if (details instanceof Integer role) {
            return role;
        }
        throw new BizException(401, "鏈巿鏉冪殑璁块棶");
    }

    public static void requireRole(int... roles) {
        Integer currentRole = getRole();
        boolean matched = Arrays.stream(roles).anyMatch(role -> role == currentRole);
        if (!matched) {
            throw new BizException(403, "鏃犳潈鎿嶄綔");
        }
    }
}
