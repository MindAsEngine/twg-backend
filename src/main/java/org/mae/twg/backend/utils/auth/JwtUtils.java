package org.mae.twg.backend.utils.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.exceptions.TokenValidationException;
import org.mae.twg.backend.models.admin.Admin;
import org.mae.twg.backend.models.auth.RefreshToken;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.repositories.auth.RefreshTokenRepo;
import org.mae.twg.backend.utils.config.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtUtils {
    @Value("${config.jwt.secret_key}")
    private String jwtSigningKey;
    @NonNull
    private final RefreshTokenRepo refreshTokenRepo;
    @NonNull
    private final ConfigService configService;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("role", customUserDetails.getUserRole());
        }
        if (userDetails instanceof Admin customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("role", customUserDetails.getAdminRole());
        }
        return generateToken(claims, userDetails);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + configService.getAccessExpiration() * 3600 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(configService.getRefreshExpiration() * 3600));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenRepo.saveAndFlush(refreshToken);
        return refreshToken;
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new TokenValidationException("Refresh token "
                    + token.getToken() +" was expired. Please make a new signin request");
        }
    }
}