package com.T4.storyTracks.service;

import com.T4.storyTracks.exception.JWTAuthenticationException;
import com.T4.storyTracks.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    // Generate JWT token
    public String generateToken(User user) {
        return Jwts.builder()
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }


    public Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JWTAuthenticationException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            Object idObj = claims.get("userId");
            if (idObj == null) {
                throw new JWTAuthenticationException("Token does not contain userId");
            }

            return Long.valueOf(idObj.toString());

        } catch (Exception e) {
            throw new JWTAuthenticationException("Invalid or expired token: " + e.getMessage());
        }
    }

}