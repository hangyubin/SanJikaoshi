package com.smartexam.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    
    // JWT密钥，实际应用中应该放在配置文件中
    private String secretString = "smart-exam-system-secret-key-1234567890123456789012345678901234567890";
    // 转换为Key对象
    private Key secret = new SecretKeySpec(secretString.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    
    // JWT过期时间，7天
    private long expiration = 604800000;
    
    // 生成JWT令牌
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("created", new Date());
        return generateToken(claims);
    }
    
    // 根据Claims生成JWT令牌
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(secret, SignatureAlgorithm.HS512)
                .compact();
    }
    
    // 生成过期时间
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration);
    }
    
    // 从JWT令牌中获取用户名
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    
    // 从JWT令牌中获取Claims
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            // 使用新的API获取Claims
            claims = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
    
    // 验证JWT令牌是否有效
    public boolean validateToken(String token, String username) {
        String usernameFromToken = getUsernameFromToken(token);
        return usernameFromToken != null && usernameFromToken.equals(username) && !isTokenExpired(token);
    }
    
    // 检查JWT令牌是否过期
    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }
    
    // 从JWT令牌中获取过期时间
    private Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }
    
    // 刷新JWT令牌
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                claims.put("created", new Date());
                refreshedToken = generateToken(claims);
            } else {
                refreshedToken = null;
            }
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }
}