package com.example.everguide.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash(value = "SocialRefresh")
public class SocialRefreshToken {

    @Id
    private String userId;

    private String refreshToken;

    @TimeToLive(unit = TimeUnit.DAYS)
    private long ttl;
}
