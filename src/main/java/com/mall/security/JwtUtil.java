package com.mall.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private static final String SECRET = "myVeryLongSecretKeyThatIsAtLeast32CharactersLongForJWT";

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(Long userId){
        return Jwts.builder()
                .setSubject(userId.toString())
                .signWith(key)
                .compact();
    }

    public static String parse(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
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
}
