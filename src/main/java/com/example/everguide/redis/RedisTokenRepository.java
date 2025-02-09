package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {

    Optional<RedisToken> findByAccessToken(String token);

    Optional<RedisToken> findByRefreshToken(String refreshToken);

    void deleteByAccessToken(String token);
}
