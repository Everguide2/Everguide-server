package com.example.everguide.redis;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLocalTokenRepository redisLocalTokenRepository;
    private final RedisSocialAccessTokenRepository redisSocialAccessTokenRepository;
    private final RedisSocialRefreshTokenRepository redisSocialRefreshTokenRepository;
    private final SmsCertificationCodeRepository smsCertificationCodeRepository;

    public void setSmsCertificationCode(String toPhoneNumber, String certificationCode) {

        SmsCertificationCode smsCertificationCode = SmsCertificationCode.builder()
                .toPhoneNumber(toPhoneNumber)
                .certificationCode(certificationCode)
                .build();
        smsCertificationCodeRepository.save(smsCertificationCode);
    }

    public String getSmsCertificationCode(String toPhoneNumber){

        Optional<SmsCertificationCode> smsCertificationCodeOpt = smsCertificationCodeRepository.findByToPhoneNumber(toPhoneNumber);

        if (smsCertificationCodeOpt.isPresent()){
            return smsCertificationCodeOpt.get().getCertificationCode();
        } else {
            return null;
        }
    }

    public void deleteSmsCertificationCode(String toPhoneNumber){

        SmsCertificationCode smsCertificationCode = smsCertificationCodeRepository.findByToPhoneNumber(toPhoneNumber).orElseThrow(EntityNotFoundException::new);
        smsCertificationCodeRepository.delete(smsCertificationCode);
    }

    // - - - - -

    public void setLocalRefreshToken(String accessToken, String refreshToken, Long expiredTime){

        RedisLocalToken redisLocalToken = RedisLocalToken.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .ttl(expiredTime)
                                        .build();
        redisLocalTokenRepository.save(redisLocalToken);
    }

    public void changeLocalRefreshToken(String originalAccessToken, String accessToken, String refreshToken, Long expiredTime){

        RedisLocalToken originalRedisLocalToken = redisLocalTokenRepository.findByAccessToken(originalAccessToken).orElseThrow(EntityNotFoundException::new);
        redisLocalTokenRepository.delete(originalRedisLocalToken);

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
