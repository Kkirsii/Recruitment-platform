package com.kkirsii.recruitmentplatform.util;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "Kkirsii";
    private static final long EXPIRATION_TIME = 86400000L; // 1天，单位：毫秒

    // 生成 JWT，包含 email
    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject("user")
                .claim("email", email)
                .claim("role", role) // 添加角色
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 解析 JWT，获取 email
    public static String getEmail(String token) {
        return (String) getClaims(token).get("email");
    }

    public static String getRole(String token) {
        return (String) getClaims(token).get("role");
    }

    // 获取 claims
    public static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // 校验 token 是否有效
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
