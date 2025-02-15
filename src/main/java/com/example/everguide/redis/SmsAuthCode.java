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
@RedisHash(value = "smsAuth")
public class SmsAuthCode {

    @Id
    private String toPhoneNumber;

    private String authCode;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private long ttl;
}
