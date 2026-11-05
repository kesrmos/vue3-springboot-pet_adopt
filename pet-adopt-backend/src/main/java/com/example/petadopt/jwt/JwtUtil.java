package com.example.petadopt.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类（jjwt 0.11.5）
 * payload 中保存 userId、username、role
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /** 有效期（毫秒） */
    @Value("${jwt.expire}")
    private long expire;

    private Key key;

    @PostConstruct
    public void init() {
        // HS256 要求密钥长度至少 32 字节；配置不合法时直接启动失败，避免运行期才报 500
        byte[] secretBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("jwt.secret 长度必须 >= 32 字节，请检查 application.yml");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * 生成 token
     */
    public String createToken(Long userId, String username, Integer role) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expire);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 token，签名错误/过期会抛异常，由拦截器处理
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** 从 payload 取用户 id（jjwt 可能解析为 Integer/Long，统一转成 Long） */
    public Long getUserId(Claims claims) {
        Object value = claims.get("userId");
        if (value == null) {
            throw new IllegalArgumentException("token 缺少 userId");
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("token 中 userId 格式不正确");
        }
    }

    /** 从 payload 取角色 */
    public Integer getRole(Claims claims) {
        Object value = claims.get("role");
        if (value == null) {
            throw new IllegalArgumentException("token 缺少 role");
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.valueOf(String.valueOf(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("token 中 role 格式不正确");
        }
    }

    /** 从 payload 取用户名，取不到返回 null（不抛异常） */
    public String getUsername(Claims claims) {
        Object value = claims.get("username");
        return value == null ? null : String.valueOf(value);
    }
}
