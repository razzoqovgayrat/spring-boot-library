package com.library.service;

import com.library.dto.Jwt;
import com.library.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${app.jwt.tokenLiveTime}")
    private int tokenLiveTime; // 10-day
    @Value("${app.jwt.secretKey}")
    private String secretKey;
//    private long refreshTokenLiveTime = 1000L * 3600 * 24 * 30; // 30-day

    public String encode(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignInKey())
                .compact();
    }

    public Jwt decode(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        long userId = claims.get("userId", Long.class);
        return new Jwt(userId);
    }

    public SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
