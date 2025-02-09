package com.example.everguide.service.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setToken(String userId, String refreshToken,Long expiredTime){

        redisTemplate.opsForValue().set(userId, refreshToken, expiredTime, TimeUnit.MILLISECONDS);
    }

    @Transactional(readOnly = true)
    public String getToken(String userId){

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        return (String) valueOperations.get(userId);
    }

    public void deleteToken(String userId){

        redisTemplate.delete(userId);
    }
}
