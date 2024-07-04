package net.cproduction.oauthsystem.token;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Date;
import lombok.RequiredArgsConstructor;
import net.cproduction.oauthsystem.auth.OAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessToken-expiredTime}")
    private long accessTokenExpiredTime;

    @Value("${jwt.refreshToken-expiredTime}")
    private long refreshTokenExpiredTime;

    private final OAuth2UserService userService;
    private final RefreshTokenService refreshTokenService;

    public TokenPair createTokenPair(String username) {

        var accessToken = generateAccessToken(username);
        var refreshToken = generateRefreshToken(username);

        return new TokenPair(accessToken, refreshToken);
    }

    private String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredTime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private String generateRefreshToken(String username) {
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredTime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        refreshTokenService.saveRefreshToken(username,refreshToken);

        return refreshToken;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token has expired");
        } catch (SecurityException e) {
            throw new TokenValidationException("Invalid token signature");
        } catch (UnsupportedJwtException e) {
            throw new TokenValidationException("Unsupported token");
        } catch (MalformedJwtException e) {
            throw new TokenValidationException("Malformed token");
        } catch (IllegalArgumentException e) {
            throw new TokenValidationException("Invalid token");
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        // TODO
        return null;
    }

    public TokenPair refreshAccessToken(String refreshToken) {
        var username = getUsernameFromToken(refreshToken);

        var savedRefreshToken = refreshTokenService.getRefreshToken(username);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new InvalidRefreshToken("Invalid Refresh Token");
        }

        refreshTokenService.deleteRefreshToken(username);

        return createTokenPair(username);
    }
}
