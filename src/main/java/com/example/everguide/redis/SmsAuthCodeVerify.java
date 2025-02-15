package com.example.everguide.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash(value = "smsVerify", timeToLive = 3600L)
public class SmsAuthCodeVerify {

    @Id
    private String toPhoneNumber;

    private String authCode;
}
