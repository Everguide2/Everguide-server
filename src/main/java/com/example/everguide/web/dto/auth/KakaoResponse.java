package com.example.everguide.web.dto.auth;

import com.example.everguide.domain.enums.ProviderType;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
        this.attribute.put("id", attribute.get("id"));
    }

    @Override
    public String getName() {

        if (!attribute.containsKey("name")) {
            return null;
        }

        return attribute.get("name").toString();
    }

    @Override
    public String getBirthday() {

        if (!attribute.containsKey("birthday")) {
            return null;
        }

        return attribute.get("birthday").toString();
    }

    @Override
    public String getBirthyear() {

        if (!attribute.containsKey("birthyear")) {
            return null;
        }

        return attribute.get("birthyear").toString();
    }

    @Override
    public String getPhoneNumber() {

        if (!attribute.containsKey("phone_number")) {
            return null;
        }

        return attribute.get("phone_number").toString();
    }

    @Override
    public String getEmail() {

        if (!attribute.containsKey("email")) {
            return null;
        }

        return attribute.get("email").toString();
    }

    @Override
    public ProviderType getProviderType() {

        return ProviderType.KAKAO;
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }
}
