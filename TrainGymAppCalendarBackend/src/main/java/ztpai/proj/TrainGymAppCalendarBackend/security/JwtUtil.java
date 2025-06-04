package ztpai.proj.TrainGymAppCalendarBackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationInMs;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpirationInMs;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", user.getAdmin());
        claims.put("trainer", user.getTrainer());

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getMail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
            .setSubject(user.getMail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationInMs))
            .signWith(SignatureAlgorithm.HS512, refreshSecret)
            .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extractUsernameFromRefresh(String token) {
        return getRefreshClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        final String username = extractUsernameFromRefresh(token);
        return username.equals(userDetails.getUsername()) && !isRefreshExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private boolean isRefreshExpired(String token) {
        return getRefreshClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }

    private Claims getRefreshClaims(String token) {
        return Jwts.parser()
            .setSigningKey(refreshSecret)
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isAdmin(String token) {
        return (Boolean) getClaims(token).get("admin");
    }

    public boolean isTrainer(String token) {
        return (Boolean) getClaims(token).get("trainer");
    }
}
