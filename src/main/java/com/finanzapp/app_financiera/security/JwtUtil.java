package com.finanzapp.app_financiera.security;

import com.finanzapp.app_financiera.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {
    private final SecretKey ACCESS_TOKEN_KEY = Keys.hmacShaKeyFor(
            "casincolavestidade4uniforme5u1ndiaviunav3acasincolavestidade4uniforme5".getBytes(StandardCharsets.UTF_8)
    );
    private final SecretKey RECOVERY_TOKEN_KEY = Keys.hmacShaKeyFor(
            "casincolavestidade4uniforme5u1ndiaviunav3acasincolavestidade4uniforme5".getBytes(StandardCharsets.UTF_8)
    );

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60; // 1 hora
    private final long RECOVERY_TOKEN_VALIDITY = 1000 * 60 * 10; // 10 minutos

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_KEY, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRecoveryToken(User user) {
        return generateToken(user, RECOVERY_TOKEN_KEY, RECOVERY_TOKEN_VALIDITY);
    }

    public Optional<String> extractEmailFromAccessToken(String token) {
        Optional<String> e = extractEmail(token, ACCESS_TOKEN_KEY);
        System.out.println("BBBBB--.. " + e.orElse(null));
        return e;
    }

    public Optional<String> extractEmailFromRecoveryToken(String token) {
        return extractEmail(token, RECOVERY_TOKEN_KEY);
    }

    public boolean validateAccessToken(String token, User user) {
        return extractEmailFromAccessToken(token)
                .map(email -> email.equals(user.getEmail()) && !isExpired(token, ACCESS_TOKEN_KEY))
                .orElse(false);
    }

    public boolean validateRecoveryToken(String token, User user) {
        return extractEmailFromRecoveryToken(token)
                .map(email -> email.equals(user.getEmail()) && !isExpired(token, RECOVERY_TOKEN_KEY))
                .orElse(false);
    }

    private String generateToken(User user, SecretKey key, long validity) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
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
