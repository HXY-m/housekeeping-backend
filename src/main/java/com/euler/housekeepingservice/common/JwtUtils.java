package com.euler.housekeepingservice.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT (JSON Web Token) 工具类
 */
@Component
public class JwtUtils {

    // 密钥 (企业级开发中应配置在 application.yml 中，且长度必须大于 32 字节)
    private static final String SECRET = "EulerHousekeepingServiceSecretKey20260314";
    // Token 有效期：24小时 (单位：毫秒)
    private static final long EXPIRATION = 86400000L;

    /**
     * 生成安全的 HMAC-SHA256 签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发 Token
     */
    public String generateToken(Long userId, String username, Integer role) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId) // 存入用户ID
                .claim("role", role)     // 存入角色
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析并校验 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}