package com.techweb.authservice.util;

import com.techweb.authservice.model.User;
import com.techweb.authservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    public static final String SECRET_KEY = "1hr6UcUYZXletjebwPukzzn+we3ghVwanU79vbmwNSY="; // For access tokens
    public static final String REFRESH_SECRET_KEY = "xLoVdnjoyqDp4JS9pxcGC6OfmHv/6Pczw7D6g1OahPg="; // For refresh tokens

    @Autowired
    public UserRepository userRepository;

    // Generate Access Token (short-lived)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList()));
        return generateToken(claims, userDetails, SECRET_KEY, 60000); // 1 min expiry
    }

    // Generate Refresh Token (long-lived)
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, REFRESH_SECRET_KEY, 604800000); // 7 days expiry
    }

    // Generic Token Generator
    private String generateToken(Map<String, Object> claims, UserDetails userDetails, String key, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    //Generic Token by Oauth 2
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", Collections.singletonList(role.replace("ROLE_", ""))); // Ensure role format is correct

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour validity
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate Access Token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token, SECRET_KEY);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, SECRET_KEY);
    }

    // Validate Refresh Token
    public boolean validateRefreshToken(String token) {
        return !isTokenExpired(token, REFRESH_SECRET_KEY);
    }

    // Extract Username from Token
    public String extractUsername(String token, String key) {
        return extractClaim(token, key, Claims::getSubject);
    }

    // Extract Expiration Date
    public Date extractExpiration(String token, String key) {
        return extractClaim(token, key, Claims::getExpiration);
    }

    // Extract Specific Claim
    public <T> T extractClaim(String token, String key, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    // Extract All Claims
    private Claims extractAllClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if Token is Expired
    private boolean isTokenExpired(String token, String key) {
        return extractExpiration(token, key).before(new Date());
    }
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token, SECRET_KEY);
        return claims.get("roles", List.class); // Extract roles claim
    }


    // Get Signing Key
    private Key getSigningKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
