package com.echoforcode.demobank.service;
/*
generate tokens
 */
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMs;
    public JwtService(@Value("${jwtSecret}") String secret,
                      @Value("${jwtExpirationMs:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        if (expirationMs <= 0) {
            throw new IllegalArgumentException("JWT lifetime must be positive");
        }
        this.expirationMs = expirationMs;
    }
    public String generateToken(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }
    public Claims extractClaims(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        if (claims.getExpiration() == null ||
                !claims.getExpiration().after(new Date()) ||
                claims.getSubject() == null ||
                claims.getSubject().isBlank()) {
            throw new JwtException("Missing or invalid required claims");
        }
        return claims;
    }
    public String extractSubject(String token) {
        return extractClaims(token).getSubject();}
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
            }


    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
