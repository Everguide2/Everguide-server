package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisSocialAccessTokenRepository extends CrudRepository<RedisSocialAccessToken, String> {

    Optional<RedisSocialAccessToken> findByUserId(String userId);
}
