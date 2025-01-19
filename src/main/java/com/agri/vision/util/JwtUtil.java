// package com.agri.vision.util;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import java.util.Date;

// public class JwtUtil {

//     private static String secretKey = "abc"; // Store this in an environment variable

//     public static String generateToken(String username) {
//         return Jwts.builder()
//                 .setSubject(username)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiry
//                 .signWith(SignatureAlgorithm.HS256, secretKey)
//                 .compact();
//     }

//     public static String extractUsername(String token) {
//         return Jwts.parser()
//                 .setSigningKey(secretKey)
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getSubject();
//     }

//     public boolean isTokenExpired(String token) {
//         return extractExpiration(token).before(new Date());
//     }

//     public Date extractExpiration(String token) {
//         return Jwts.parser()
//                 .setSigningKey(secretKey)
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getExpiration();
//     }

//     public boolean validateToken(String token, String username) {
//         return (username.equals(extractUsername(token)) && !isTokenExpired(token));
//     }
// }
