package com.euler.housekeepingservice.security;

import com.euler.housekeepingservice.common.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 全局安保门禁 (每次请求都会经过这里)
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 获取请求头中的 Authorization 字段
        String header = request.getHeader("Authorization");

        // 2. 检查是否是以 "Bearer " 打头的标准 Token
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7); // 剔除 "Bearer " 前缀，提取真实 token
            try {
                // 3. 解析 Token
                Claims claims = jwtUtils.parseToken(token);
                Long userId = claims.get("userId", Long.class);

                // 4. 如果解析成功，将用户信息存入 Spring Security 的上下文中 (代表已登录)
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 注意：这里的 Principal (主体) 我们放了 userId，方便后续在 Controller 里直接获取
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token 过期或被篡改，不要抛异常终止程序，直接放行，交由后续的 Security 鉴权机制去拦截报错
                logger.warn("Token 解析失败或已过期: " + e.getMessage());
            }
        }

        // 5. 无论有没有 Token，都必须让请求继续往下走（交给 Spring Security 去判断到底放不放行）
        filterChain.doFilter(request, response);
    }
}
