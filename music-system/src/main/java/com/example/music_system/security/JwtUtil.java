package com.example.music_system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key secretKey;

    public JwtUtil(@Value("${JWT_SECRET}") String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET is not set or empty.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        System.out.println("JWT secret key initialized.");
    }

    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 часов
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
        System.out.println("Generated JWT token for user '" + username + "': " + token);
        return token;
    }

    public String extractUsername(String token) {
        String username = extractAllClaims(token).getSubject();
        System.out.println("Extracted username from token: " + username);
        return username;
    }

    private Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("Extracted claims: " + claims);
            return claims;
        } catch (JwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            System.out.println("JWT token is valid.");
            return true;
        } catch (JwtException e) {
            System.err.println("JWT token validation failed: " + e.getMessage());
            return false; // Invalid token
        }
    }

    // Извлечение имени пользователя из токена
    public String extractUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return extractUsername(token.substring(7));
        }
        throw new RuntimeException("Invalid Authorization header format");
    }
}
