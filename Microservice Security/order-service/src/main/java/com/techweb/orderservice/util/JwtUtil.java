package com.techweb.orderservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "1hr6UcUYZXletjebwPukzzn+we3ghVwanU79vbmwNSY="; // Same as Auth Service

    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)); // FIXED

    /**
     * Extract Claims from JWT Token
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extract Username (Subject) from JWT Token
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validate JWT Token
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            if (claims.getExpiration().before(new Date())) {
                System.out.println("JWT Token is expired!");
                return false;
            }
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token Expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT Token: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("JWT Signature does not match: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT Token validation failed: " + e.getMessage());
        }
        return false;
    }
}

