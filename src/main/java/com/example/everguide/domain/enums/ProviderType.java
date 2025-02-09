package com.example.everguide.domain.enums;

public enum ProviderType {
    LOCAL, NAVER, KAKAO;

    public static ProviderType getProviderType(String providerTypeStr) {
        return switch (providerTypeStr) {
            case "naver" -> ProviderType.NAVER;
            case "kakao" -> ProviderType.KAKAO;
            case "local" -> ProviderType.LOCAL;
            default -> throw new IllegalArgumentException("providerType을 확인할 수 없습니다 : " + providerTypeStr);
        };
    }
}
