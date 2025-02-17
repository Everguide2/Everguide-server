package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LocalRefreshTokenRepository extends CrudRepository<LocalRefreshToken, String> {

    Optional<LocalRefreshToken> findByUserId(String userId);

    void deleteByUserId(String userId);
}
