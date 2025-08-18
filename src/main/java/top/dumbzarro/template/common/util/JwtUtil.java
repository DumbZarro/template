package top.dumbzarro.template.common.util;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    public static String generateToken(String subject, Map<String, Object> claims, long expirationMs, Key signingKey) {
        return Jwts.builder()
                .subject(subject) // 主题（Subject，用户唯一标识）
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public static Map<String, Object> parseToken(String token, Key verificationKey) {
        return Jwts.parser()
                .verifyWith((SecretKey) verificationKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
