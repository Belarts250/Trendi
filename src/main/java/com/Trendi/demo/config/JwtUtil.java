package com.Trendi.demo.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.*;

@Component  // Makes this a Spring-managed bean (singleton)
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    // @Value injects values from application.properties
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    /** gener
     * GENERATE a JWT token for a user.
     * @param email - the user's email (subject of the token)
     * @param userId - stored as a claim
     * @return a signed JWT string
     */
    public String generateToken(String email, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)                              // "sub" claim = email
                .claim("userId", userId)                    // Custom claim
                .issuedAt(now)                               // "iat" = issued at
                .expiration(expiryDate)                      // "exp" = expiration time
                .signWith(secretKey)                         // Sign with HMAC-SHA256
                .compact();                                  // Build the token string
    }

    /**
     * EXTRACT the email (subject) from a token.
     */
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * EXTRACT the userId claim from a token.
     */
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * VALIDATE a token: check if it's well-formed and not expired.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // Token is invalid or expired
        }
    }

    /**
     * Helper method to parse and verify the token.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)    // Verify the signature
                .build()
                .parseSignedClaims(token)  // Parse the token
                .getPayload();             // Get the claims (payload)
    }
}