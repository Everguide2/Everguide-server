package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsAuthCodeRepository extends CrudRepository<SmsAuthCode, String> {

    Optional<SmsAuthCode> findByToPhoneNumber(String toPhoneNumber);
}
