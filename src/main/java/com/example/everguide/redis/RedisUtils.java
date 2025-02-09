package com.example.everguide.redis;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTokenRepository redisTokenRepository;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate, RedisTokenRepository redisTokenRepository) {
        this.redisTemplate = redisTemplate;
        this.redisTokenRepository = redisTokenRepository;
    }

    public void setLocalRefreshToken(String accessToken, String refreshToken, Long expiredTime){

        RedisToken redisToken = RedisToken.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .ttl(expiredTime)
                                        .build();
        redisTokenRepository.save(redisToken);
    }

    public String getLocalRefreshToken(String accessToken){

        Optional<RedisToken> redisTokenOptional = redisTokenRepository.findByAccessToken(accessToken);

        if (redisTokenOptional.isPresent()){
            return redisTokenOptional.get().getRefreshToken();
        } else {
            return null;
        }
    }

    public Boolean existsLocalRefreshToken(String refreshToken){

        return redisTokenRepository.findByRefreshToken(refreshToken).isPresent();
    }

    public void deleteLocalRefreshToken(String accessToken){

        RedisToken redisToken = redisTokenRepository.findByAccessToken(accessToken).orElseThrow(EntityNotFoundException::new);
        redisTokenRepository.delete(redisToken);
    }

    // - - - - -

    public String getSocialAccessToken(String userId) {

        return (String) redisTemplate.opsForHash().get(userId, "access");
    }

    public String getSocialRefreshToken(String userId) {

        return (String) redisTemplate.opsForHash().get(userId, "refresh");
    }

    public void setSocialAccessToken(String userId, String token){

        redisTemplate.opsForHash().put(userId, "access", token);
    }

    public void setSocialRefreshToken(String userId, String token){

        redisTemplate.opsForHash().put(userId, "refresh", token);
    }

    public void deleteSocialTokens(String userId){

        redisTemplate.opsForHash().delete(userId, "access");
        redisTemplate.opsForHash().delete(userId, "refresh");
    }
}
