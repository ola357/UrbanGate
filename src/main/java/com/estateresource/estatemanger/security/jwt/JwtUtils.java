package com.estateresource.estatemanger.security.jwt;

import com.estateresource.estatemanger.security.configuration.SecurityApplicationConfigs;
import com.estateresource.estatemanger.security.model.entity.RefreshToken;
import com.estateresource.estatemanger.security.model.enums.TokenType;
import com.estateresource.estatemanger.security.repository.RefreshTokenRepository;
import com.estateresource.estatemanger.shared.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUtils {


    private final SecurityApplicationConfigs securityApplicationConfigs;
    private final RefreshTokenRepository refreshTokenRepository;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("estate", user.getEstateId());
        return createToken(claims, user.getPhoneNumber());
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .claim("phone_number", subject)
                .claim("tokenType", TokenType.ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000))
                .signWith(signingKey())
                .compact();
    }


    public String generateRefreshToken(UserDetails user) {

        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .subject(user.getUsername())
                .id(jti)
                .claim("tokenType", TokenType.REFRESH)
                .issuedAt(new Date())
                .expiration(Date.from(
                        Instant.now().plus(5, ChronoUnit.DAYS)))
                .signWith(signingKey())
                .compact();

        RefreshToken refreshToken = RefreshToken.builder()
                .username(user.getUsername())
                .jti(jti)
                .expiresAt(Timestamp.from(Instant.now().plus(5, ChronoUnit.DAYS)))
                .revoked(false)
                .build();
        refreshToken.prepareForInsert();
        refreshTokenRepository.insert(refreshToken);

        return token;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        log.debug("Verifying if token is valid: {}", username);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(
                securityApplicationConfigs.getJwtSignSecrets()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }
}
