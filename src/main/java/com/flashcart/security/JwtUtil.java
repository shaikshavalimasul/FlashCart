package com.flashcart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(
            String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        System.currentTimeMillis()
                                + expiration))
                .signWith(getSigningKey(),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(
            String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            boolean notExpired =
                    !extractClaims(token)
                            .getExpiration()
                            .before(new Date());
            return tokenEmail.equals(email)
                    && notExpired;
        } catch (Exception e) {
            return false;
        }
    }
}

//
//
//Topic 32 (JwtUtil) is the hardest
//code we have written so far.
//
//It involves:
//        → Cryptography concepts
//→ Builder pattern (multiple chains)
//→ Java 8 lambdas
//→ New library (jjwt)
//→ Security concepts
//→ Long method chains
//
//Even experienced developers
//take time to understand this.
//
//You are NOT behind.
//This IS genuinely difficult.




//
//WHAT IT DOES (must know):
//        → generateToken() → creates JWT token after login
//→ extractEmail() → reads who the token belongs to
//→ validateToken() → checks if token is valid/not expired
//
//HOW IT WORKS (concept only):
//        → Token = header.payload.signature
//→ Signature = math using secret key
//→ Verification = recalculate and compare
//
//WHAT YOU DON'T NEED TO MEMORIZE:
//        → Every line of the implementation
//→ Exact jjwt API method names
//→ The cryptography internals
//
//In real companies:
//Senior developers write JwtUtil ONCE.
//Junior developers USE it by calling:
//        jwtUtil.generateToken(email, role)
//jwtUtil.validateToken(token, email)



//
//Think of JwtUtil like a SAFE LOCKER:
//
//You don't need to know HOW the locker
//mechanism works internally.
//
//You need to know:
//        → How to LOCK it (generateToken)
//→ How to UNLOCK it (validateToken)
//→ How to read what's inside (extractEmail)
//
//The internal mechanics (cryptography)
//= the lock mechanism you don't see
//
//Your job = use the locker correctly
//        Not = understand every gear inside


//
//What To Do Right Now
//
//Step 1 — Don't delete what you typed.
//
//JwtUtil.java is correct. Keep it.
//
//Step 2 — Just understand these 3 things:
//
//        1. generateToken(email, role)
//   → I give it: email + role
//   → It gives me: a JWT string
//   → I send that string to the logged-in user
//
//2. validateToken(token, email)
//   → I give it: the token + email
//   → It gives me: true (valid) or false (invalid)
//        → I use this to protect APIs
//
//3. extractEmail(token)
//   → I give it: a JWT token
//   → It gives me: the email inside the token
//   → I use this to know WHO is making the request
//
//That's all you need for now.