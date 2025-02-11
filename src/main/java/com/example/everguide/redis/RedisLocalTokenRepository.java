package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisLocalTokenRepository extends CrudRepository<RedisLocalToken, String> {

    Optional<RedisLocalToken> findByAccessToken(String token);

    Optional<RedisLocalToken> findByRefreshToken(String refreshToken);

    void deleteByAccessToken(String token);
}
