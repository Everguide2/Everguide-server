package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisSocialRefreshTokenRepository extends CrudRepository<RedisSocialRefreshToken, String> {

    Optional<RedisSocialRefreshToken> findByUserId(String userId);
}
