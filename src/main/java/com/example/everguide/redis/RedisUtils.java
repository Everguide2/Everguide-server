package com.example.everguide.redis;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLocalTokenRepository redisLocalTokenRepository;
    private final RedisSocialAccessTokenRepository redisSocialAccessTokenRepository;
    private final RedisSocialRefreshTokenRepository redisSocialRefreshTokenRepository;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate, RedisLocalTokenRepository redisLocalTokenRepository, RedisSocialAccessTokenRepository redisSocialAccessTokenRepository, RedisSocialRefreshTokenRepository redisSocialRefreshTokenRepository) {
        this.redisTemplate = redisTemplate;
        this.redisLocalTokenRepository = redisLocalTokenRepository;
        this.redisSocialAccessTokenRepository = redisSocialAccessTokenRepository;
        this.redisSocialRefreshTokenRepository = redisSocialRefreshTokenRepository;
    }

    public void setLocalRefreshToken(String accessToken, String refreshToken, Long expiredTime){

        RedisLocalToken redisLocalToken = RedisLocalToken.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .ttl(expiredTime)
                                        .build();
        redisLocalTokenRepository.save(redisLocalToken);
    }

    public String getLocalRefreshToken(String accessToken){

        Optional<RedisLocalToken> redisTokenOptional = redisLocalTokenRepository.findByAccessToken(accessToken);

        if (redisTokenOptional.isPresent()){
            return redisTokenOptional.get().getRefreshToken();
        } else {
            return null;
        }
    }

    public Boolean existsLocalRefreshToken(String refreshToken){

        return redisLocalTokenRepository.findByRefreshToken(refreshToken).isPresent();
    }

    public void deleteLocalRefreshToken(String accessToken){

        RedisLocalToken redisLocalToken = redisLocalTokenRepository.findByAccessToken(accessToken).orElseThrow(EntityNotFoundException::new);
        redisLocalTokenRepository.delete(redisLocalToken);
    }

    // - - - - -

    public String getSocialAccessToken(String userId) {

        Optional<RedisSocialAccessToken> redisSocialAccessTokenOpt = redisSocialAccessTokenRepository.findByUserId(userId);

        if (redisSocialAccessTokenOpt.isPresent()){
            return redisSocialAccessTokenOpt.get().getAccessToken();
        } else {
            return null;
        }
    }

    public String getSocialRefreshToken(String userId) {

        Optional<RedisSocialRefreshToken> redisSocialRefreshTokenOpt = redisSocialRefreshTokenRepository.findByUserId(userId);

        if (redisSocialRefreshTokenOpt.isPresent()){
            return redisSocialRefreshTokenOpt.get().getRefreshToken();
        } else {
            return null;
        }
    }

    public void setSocialAccessToken(String userId, String accessToken, Long expiredTime) {

        RedisSocialAccessToken redisSocialAccessToken = RedisSocialAccessToken.builder()
                .userId(userId)
                .accessToken(accessToken)
                .ttl(expiredTime)
                .build();
        redisSocialAccessTokenRepository.save(redisSocialAccessToken);
    }

    public void setSocialRefreshToken(String userId, String refreshToken, Long expiredTime) {

        RedisSocialRefreshToken redisSocialRefreshToken = RedisSocialRefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .ttl(expiredTime)
                .build();
        redisSocialRefreshTokenRepository.save(redisSocialRefreshToken);
    }

    public void deleteSocialTokens(String userId){

        RedisSocialAccessToken redisSocialAccessToken = redisSocialAccessTokenRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        redisSocialAccessTokenRepository.delete(redisSocialAccessToken);

        RedisSocialRefreshToken redisSocialRefreshToken = redisSocialRefreshTokenRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        redisSocialRefreshTokenRepository.delete(redisSocialRefreshToken);
    }
}
