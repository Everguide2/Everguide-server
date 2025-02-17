package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SocialAccessTokenRepository extends CrudRepository<SocialAccessToken, String> {

    Optional<SocialAccessToken> findByUserId(String userId);
}
