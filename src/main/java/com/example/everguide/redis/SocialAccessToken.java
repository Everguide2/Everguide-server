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
@RedisHash(value = "SocialAccess")
public class SocialAccessToken {

    @Id
    private String userId;

    private String accessToken;

    @TimeToLive(unit = TimeUnit.HOURS)
    private long ttl;
}
