package com.example.everguide.redis;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;
    private final LocalRefreshTokenRepository localRefreshTokenRepository;
    private final SocialAccessTokenRepository socialAccessTokenRepository;
    private final SocialRefreshTokenRepository socialRefreshTokenRepository;
    private final SmsAuthCodeRepository smsAuthCodeRepository;
    private final SmsAuthCodeVerifyRepository smsAuthCodeVerifyRepository;

    public void setSmsAuthCode(String toPhoneNumber, String authCode, Long expiredTime) {

        SmsAuthCode smsAuthCode = SmsAuthCode.builder()
                .toPhoneNumber(toPhoneNumber)
                .authCode(authCode)
                .ttl(expiredTime)
                .build();
        smsAuthCodeRepository.save(smsAuthCode);
    }

    public void setSmsAuthCodeVerify(String toPhoneNumber, String authCode, Long expiredTime) {

        SmsAuthCodeVerify smsAuthCodeVerify = SmsAuthCodeVerify.builder()
                .toPhoneNumber(toPhoneNumber)
                .authCode(authCode)
                .ttl(expiredTime)
                .build();
        smsAuthCodeVerifyRepository.save(smsAuthCodeVerify);
    }

    public String getSmsAuthCode(String toPhoneNumber){

        Optional<SmsAuthCode> smsAuthCodeOpt = smsAuthCodeRepository.findByToPhoneNumber(toPhoneNumber);

        if (smsAuthCodeOpt.isPresent()){
            return smsAuthCodeOpt.get().getAuthCode();
        } else {
            return null;
        }
    }

    public String getSmsAuthCodeVerify(String toPhoneNumber){

        Optional<SmsAuthCodeVerify> smsAuthCodeVerifyOpt = smsAuthCodeVerifyRepository.findByToPhoneNumber(toPhoneNumber);

        if (smsAuthCodeVerifyOpt.isPresent()){
            return smsAuthCodeVerifyOpt.get().getAuthCode();
        } else {
            return null;
        }
    }

    public void deleteSmsAuthCode(String toPhoneNumber){

        SmsAuthCode smsAuthCode = smsAuthCodeRepository.findByToPhoneNumber(toPhoneNumber).orElseThrow(EntityNotFoundException::new);
        smsAuthCodeRepository.delete(smsAuthCode);
    }

    // - - - - -

    public void setLocalRefreshToken(String userId, String refreshToken, Long expiredTime){

        LocalRefreshToken localRefreshToken = LocalRefreshToken.builder()
                                        .userId(userId)
                                        .refreshToken(refreshToken)
                                        .ttl(expiredTime)
                                        .build();
        localRefreshTokenRepository.save(localRefreshToken);
    }

    public void changeLocalRefreshToken(String userId, String refreshToken, Long expiredTime){

        LocalRefreshToken originalLocalRefreshToken = localRefreshTokenRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        localRefreshTokenRepository.delete(originalLocalRefreshToken);

        LocalRefreshToken localRefreshToken = LocalRefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .ttl(expiredTime)
                .build();
        localRefreshTokenRepository.save(localRefreshToken);
    }

    public String getLocalRefreshToken(String userId){

        LocalRefreshToken redisToken = localRefreshTokenRepository.findByUserId(userId).orElse(null);

        if (redisToken != null){
            return redisToken.getRefreshToken();
        } else {
            return null;
        }
    }

    public Boolean existsLocalRefreshToken(String userId){

        return localRefreshTokenRepository.findByUserId(userId).isPresent();
    }

    public void deleteLocalRefreshToken(String userId){

        LocalRefreshToken localRefreshToken = localRefreshTokenRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        localRefreshTokenRepository.delete(localRefreshToken);
    }

    // - - - - -

    public String getSocialAccessToken(String userId) {

        Optional<SocialAccessToken> redisSocialAccessTokenOpt = socialAccessTokenRepository.findByUserId(userId);

        if (redisSocialAccessTokenOpt.isPresent()){
            return redisSocialAccessTokenOpt.get().getAccessToken();
        } else {
            return null;
        }
    }

    public String getSocialRefreshToken(String userId) {

        Optional<SocialRefreshToken> redisSocialRefreshTokenOpt = socialRefreshTokenRepository.findByUserId(userId);

        if (redisSocialRefreshTokenOpt.isPresent()){
            return redisSocialRefreshTokenOpt.get().getRefreshToken();
        } else {
            return null;
        }
    }

    public void setSocialAccessToken(String userId, String accessToken, Long expiredTime) {

        SocialAccessToken socialAccessToken = SocialAccessToken.builder()
                .userId(userId)
                .accessToken(accessToken)
                .ttl(expiredTime)
                .build();
        socialAccessTokenRepository.save(socialAccessToken);
    }

    public void setSocialRefreshToken(String userId, String refreshToken, Long expiredTime) {

        SocialRefreshToken socialRefreshToken = SocialRefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .ttl(expiredTime)
                .build();
        socialRefreshTokenRepository.save(socialRefreshToken);
    }

    public void deleteSocialAccessToken(String userId){

        SocialAccessToken socialAccessToken = socialAccessTokenRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        socialAccessTokenRepository.delete(socialAccessToken);
    }

    public void deleteSocialRefreshToken(String userId){

        SocialRefreshToken socialRefreshToken = socialRefreshTokenRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        socialRefreshTokenRepository.delete(socialRefreshToken);
    }

    public void deleteSocialTokens(String userId){

        SocialAccessToken socialAccessToken = socialAccessTokenRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        socialAccessTokenRepository.delete(socialAccessToken);

        SocialRefreshToken socialRefreshToken = socialRefreshTokenRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        socialRefreshTokenRepository.delete(socialRefreshToken);
    }
}
