package com.tinqinacademy.authentication.core.security;

import com.tinqinacademy.authentication.persistence.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Base64;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public String createToken(String id, String role) {

        Date now = new Date();
        Date validity = new Date(now.getTime() + 300_000); // make it valid for 5 minutes

        return Jwts.builder()
            .setSubject(id)
            .claim("role",role)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public JwtTokenInfo extractToken(String token) {

        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

        String tokenId = claims.getId();
        String tokenRole = claims.get("role").toString();

        return JwtTokenInfo.builder()
            .id(UUID.fromString(tokenId))
            .role(Role.getByCode(tokenRole))
            .build();
    }
}