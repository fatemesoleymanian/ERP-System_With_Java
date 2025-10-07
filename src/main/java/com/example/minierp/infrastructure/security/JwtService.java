package com.example.minierp.infrastructure.security;

import com.example.minierp.domain.user.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value(value = "${jwt.secret}")
    private String SECRET ;

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET)
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername());
    }

    public String generateResetToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("reset", true)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutes
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

}
