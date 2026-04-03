package com.mall.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "myVeryLongSecretKeyThatIsAtLeast32CharactersLongForJWT";

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 30分钟过期
    private static final long EXPIRATION = 30 * 60 * 1000;

    // 刷新阈值（10分钟）
    private static final long REFRESH_THRESHOLD = 10 * 60 * 1000;

    public static String generateToken(Long userId){
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static String generateTokenByName(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static Long parseToken(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }


    public static String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    // 是否需要刷新
    public static boolean shouldRefresh(String token) {
        Date expiration = getClaims(token).getExpiration();
        long remain = expiration.getTime() - System.currentTimeMillis();
        return remain < REFRESH_THRESHOLD;
    }
}
