package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SocialRefreshTokenRepository extends CrudRepository<SocialRefreshToken, String> {

    Optional<SocialRefreshToken> findByUserId(String userId);
}
