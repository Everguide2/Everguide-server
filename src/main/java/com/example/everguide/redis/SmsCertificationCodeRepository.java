package com.example.everguide.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsCertificationCodeRepository extends CrudRepository<SmsCertificationCode, String> {

    Optional<SmsCertificationCode> findByToPhoneNumber(String toPhoneNumber);
}
