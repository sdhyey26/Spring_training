package com.SpringSecurity.Security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.SpringSecurity.Exception.UserApiException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Data;

@Component
@Data
public class JwtTokenProvider {

    @Value("${app.jwt-private-key}")
    private String privateKeyPath;

    @Value("${app.jwt-public-key}")
    private String publicKeyPath;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    private volatile PrivateKey cachedPrivateKey;
    private volatile PublicKey cachedPublicKey;

    private PrivateKey loadPrivateKey() {
        try {
            if (cachedPrivateKey != null) {
                return cachedPrivateKey;
            }

            byte[] keyBytes = resolveKeyBytes(privateKeyPath, true);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            cachedPrivateKey = kf.generatePrivate(spec);
            return cachedPrivateKey;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
    }

    private PublicKey loadPublicKey() {
        try {
            if (cachedPublicKey != null) {
                return cachedPublicKey;
            }

            byte[] keyBytes = resolveKeyBytes(publicKeyPath, false);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            cachedPublicKey = kf.generatePublic(spec);
            return cachedPublicKey;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }

    private static byte[] readPemOrDer(Path path) throws IOException {
        byte[] allBytes = Files.readAllBytes(path);
        return normalizeKeyBytes(allBytes);
    }

    private static byte[] readPemOrDerFromClasspath(String classpathLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource(stripClasspathPrefix(classpathLocation));
        try (InputStream in = resource.getInputStream()) {
            byte[] allBytes = in.readAllBytes();
            return normalizeKeyBytes(allBytes);
        }
    }

    private static String stripClasspathPrefix(String path) {
        return path.startsWith("classpath:") ? path.substring("classpath:".length()) : path;
    }

    private static byte[] normalizeKeyBytes(byte[] rawBytes) {
        String content = new String(rawBytes).replace("\r", "");
        if (content.contains("BEGIN")) {
            String cleaned = content
                .replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----\n", "")
                .replace("-----BEGIN PUBLIC KEY-----\n", "")
                .replace("-----END PUBLIC KEY-----\n", "")
                .replace("\n", "");
            return java.util.Base64.getDecoder().decode(cleaned);
        }

        String trimmed = content.trim();
        if (trimmed.matches("[A-Za-z0-9+/=\n]+")) {
            return java.util.Base64.getDecoder().decode(trimmed.replace("\n", ""));
        }

        return rawBytes;
    }

    private static byte[] resolveKeyBytes(String pathOrClasspath, boolean isPrivate) throws IOException {
        if (pathOrClasspath.startsWith("classpath:")) {
            return readPemOrDerFromClasspath(pathOrClasspath);
        }
        return readPemOrDer(Path.of(pathOrClasspath));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .claim("role", authentication.getAuthorities())
                .signWith(loadPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(loadPublicKey()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new UserApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new UserApiException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new UserApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new UserApiException(HttpStatus.BAD_REQUEST, "JWT claims string is empty.");
        } catch (SecurityException ex) {
            throw new UserApiException(HttpStatus.BAD_REQUEST, "Invalid Credentials");
        }
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(loadPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}


