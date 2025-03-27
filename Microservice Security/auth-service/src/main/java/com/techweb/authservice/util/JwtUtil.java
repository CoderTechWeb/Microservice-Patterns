package com.techweb.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    public static final String SECRET_KEY = "1hr6UcUYZXletjebwPukzzn+we3ghVwanU79vbmwNSY="; // Replace with a long, secure key
    public static final String REFRESH_SECRET_KEY = "xLoVdnjoyqDp4JS9pxcGC6OfmHv/6Pczw7D6g1OahPg=";
    // Generate token for a user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Store roles in JWT **without** "ROLE_" prefix
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", "")) // ✅ Remove "ROLE_" from DB roles
                .collect(Collectors.toList()));
        return generateToken(claims, userDetails, SECRET_KEY, 60000);
    }

    // Generate Refresh Token (long-lived)
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, REFRESH_SECRET_KEY, 604800000); // 7 days expiry
    }

    // Generate token with extra claims
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String key, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour validity
//                .setExpiration(new Date(System.currentTimeMillis() + 10000)) // 10 seconds validity
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 1 minute validity
                .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token, SECRET_KEY);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, SECRET_KEY);
    }

    // Extract username from token
    public String extractUsername(String token, String key) {
        return extractClaim(token, key, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token, String key) {
        return extractClaim(token, key, Claims::getExpiration);
    }

    // Extract specific claim
    public <T> T extractClaim(String token, String key, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    // Extract all claims
    private Claims extractAllClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token, String key) {
        return extractExpiration(token, key).before(new Date());
    }

    // Get signing key
    private Key getSigningKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
