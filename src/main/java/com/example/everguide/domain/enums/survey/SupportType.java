package com.example.everguide.domain.enums.survey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupportType {

    PHYSICAL_HEALTH("010", "신체건강"),
    MENTAL_HEALTH("020", "정신건강"),
    LIFE_SUPPORT("030", "생활지원"),
    HOUSING("040", "주거"),
    EMPLOYMENT("050", "일자리"),
    CULTURE_LEISURE("060", "문화·여가"),
    SAFETY_CRISIS("070", "안전·위기"),
    PREGNANCY_CHILDBIRTH("080", "임신·출산"),
    CHILDCARE("090", "보육"),
    EDUCATION("100", "교육"),
    MEDICAL_TREATMENT("110", "입양·위탁"),
    PROTECTION_CARE("120", "보호·돌봄"),
    FINANCE("130", "서민금융"),
    LEGAL("140", "법률");

    private final String code;
    private final String description;

    public static SupportType fromString(String text) {
        for (SupportType type : SupportType.values()) {
            if (type.getDescription().equals(text)) {
                return type;
            }
        }
        return LIFE_SUPPORT; // 기본값 설정
    }

    public static SupportType fromCode(String code) {
        for (SupportType type : SupportType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return LIFE_SUPPORT; // 기본값 설정
    }
}
