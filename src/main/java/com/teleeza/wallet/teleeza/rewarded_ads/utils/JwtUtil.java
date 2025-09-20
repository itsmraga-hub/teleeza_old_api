package com.teleeza.wallet.teleeza.rewarded_ads.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.function.Function;
//
//@Component
//public class JwtUtil {
//
//    private static final String SECRET_KEY = "your-256-bit-secret-key"; // Replace with your actual secret key
//
//    private Key getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    // Extract claim from token
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // Extract all claims
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // Extract phone number from token
//    public String extractPhoneNumber(String token) {
//        return extractClaim(token, claims -> claims.get("phoneNumber", String.class));
//    }
//}
