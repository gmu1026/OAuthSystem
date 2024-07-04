package net.cproduction.oauthsystem.token;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refreshToken-expiredTime}")
    private long refreshTokenExpiredTime;

    public void saveRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue()
                .set("refresh_token:" + username, refreshToken, refreshTokenExpiredTime, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("refresh_token:" + username);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh_token" + username);
    }
}
