package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsAuthCodeVerifyRepository extends CrudRepository<SmsAuthCodeVerify, String> {

    Optional<SmsAuthCodeVerify> findByToPhoneNumber(String toPhoneNumber);
}
