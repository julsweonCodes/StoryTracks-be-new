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
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }


    public Long extractUserIdOrNull(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;  // anonymous user
        }

        try {
            String token = authHeader.substring(7);

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            Object idObj = claims.get("userId");

            return (idObj != null) ? Long.valueOf(idObj.toString()) : null;

        } catch (Exception e) {
            return null; // treat invalid token as anonymous
        }
    }

    public Long requireUserId(String authHeader) {
        Long userId = extractUserIdOrNull(authHeader);
        if (userId == null) {
            throw new JWTAuthenticationException("Authentication required.");
        }
        return userId;
    }


}