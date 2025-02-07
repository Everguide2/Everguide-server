package com.example.everguide.domain.enums.survey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HouseholdType {

    MULTICULTURAL("010", "다문화·탈북민"),
    SINGLE_PARENT("020", "다자녀"),
    VETERAN("030", "보훈대상자"),
    DISABLED("040", "장애인"),
    LOW_INCOME("050", "저소득"),
    SINGLE_PARENT_GRANDPARENT("060", "한부모·조손");

    private final String code;
    private final String description;

    public static HouseholdType fromString(String text) {
        for (HouseholdType type : HouseholdType.values()) {
            if (type.getDescription().equals(text)) {
                return type;
            }
        }
        return LOW_INCOME; // 기본값 설정
    }

    public static HouseholdType fromCode(String code) {
        for (HouseholdType type : HouseholdType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return LOW_INCOME; // 기본값 설정
    }
}
