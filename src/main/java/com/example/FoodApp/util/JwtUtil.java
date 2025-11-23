package com.example.FoodApp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret_key;

    @Value("${jwt.expiration}")
    private Long expiryTime;

// --- 1. TOKEN EXTRACTION METHODS (Reading Claims) ---

    // this method will extract the username from the token
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /*
    this method will extract the expiration date from the token
    @Param the token
    @Return the expiration Date from the token.
     */
    public Date extractExpiryDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /*
    Generic method to extract any single claim using a resolver function
    @Param token the JWT String
    @Param claimResolver function to map Claims object to desired value
    @Param <T> the type of the claim value
    @Return the extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /*
    parses the token, verifies the signature, and returns all claims.
    This is the core reading operation.
    @Param token The JWT String
    @Return the Claims Object (payload)
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()//because of setSigningKey
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    /*
    Converts the base64 encoded secret string into a cryptographic Key
    @Return the Signing Key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret_key);
        return Keys.hmacShaKeyFor(keyBytes);
    }


//  --- 2. TOKEN VALIDATION METHODS (Verification) ---


    /*
    Checks if the token has expired.
    @Param token
    @Return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiryDate(token).before(new Date());
    }


    /*
    check the username is same or not
    @Param jwt token
    @Param userDetails for username to compare with token of username
    @return if the both are same and check the token expiration then  true , else false
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (userDetails.getUsername().equals(username) && !isTokenExpired(token));
    }


    // --- 3. TOKEN GENERATION METHODS (Creation) ---

    /*
    generate the token. so you need userDetails
    @Param UserDetails
    @Return token as string
     */
    public String generateJwtToken(UserDetails userDetails) {
        Map<String,Object> claims=new HashMap<>();
        claims.put("role",userDetails.getAuthorities().iterator().next().getAuthority());
        return createJwtToken(claims, userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails){
        return Jwts.builder()
                .setClaims(new HashMap<String,Object>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+7L*24 * 60 * 60*1000))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    /*
    @Param claims custom claims to include in the payload
    @Param subject the token subject (usually in the payload)
    @Return jwt token
     */
    private String createJwtToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + expiryTime))
                .setIssuedAt(new Date())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}