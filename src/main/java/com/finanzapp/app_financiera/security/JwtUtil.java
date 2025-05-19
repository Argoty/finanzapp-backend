package com.finanzapp.app_financiera.security;

import com.finanzapp.app_financiera.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {
    private final SecretKey ACCESS_TOKEN_KEY;

    public JwtUtil(@Value("${access.token.key}") String accessToken) {
        this.ACCESS_TOKEN_KEY = Keys.hmacShaKeyFor(accessToken.getBytes(StandardCharsets.UTF_8));
    }

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60; // 1 hora

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(ACCESS_TOKEN_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public Optional<String> extractEmailFromAccessToken(String token) {
        Optional<String> e = extractEmail(token, ACCESS_TOKEN_KEY);
        return e;
    }

    public boolean validateAccessToken(String token, User user) {
        return extractEmailFromAccessToken(token)
                .map(email -> email.equals(user.getEmail()) && !isExpired(token, ACCESS_TOKEN_KEY))
                .orElse(false);
    }

    private Optional<String> extractEmail(String token, SecretKey key) {
        try {
            if(token.startsWith("Bearer ")){
                token = token.substring(7);
            }
            String email = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return Optional.of(email);
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println(e.getMessage() +  "-->" + token + "<--");
            return Optional.empty();
        }
    }

    private boolean isExpired(String token, SecretKey key) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
}
