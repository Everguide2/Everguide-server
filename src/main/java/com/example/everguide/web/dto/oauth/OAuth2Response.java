package com.example.everguide.web.dto.oauth;

import com.example.everguide.domain.enums.ProviderType;

public interface OAuth2Response {

    // 사용자 실명
    String getName();

    String getBirthday();

    String getBirthyear();

    String getPhoneNumber();

    String getEmail();

    // 제공자 (Ex. naver, google, ...)
    ProviderType getProviderType();

    // 제공자에서 발급해주는 아이디(번호)
    String getProviderId();
}
