package com.example.everguide.domain.enums.survey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LifeCycleType {

    INFANT("001", "영유아"),
    CHILD("002", "아동"),
    TEENAGER("003", "청소년"),
    YOUNG_ADULT("004", "청년"),
    MIDDLE_AGED("005", "중장년"),
    ELDERLY("006", "노년"),
    PREGNANCY("007", "임신·출산");

    private final String code;
    private final String description;

    public static LifeCycleType fromString(String text) {
        for (LifeCycleType type : LifeCycleType.values()) {
            if (type.getDescription().equals(text)) {
                return type;
            }
        }
        return YOUNG_ADULT; // 기본값 설정
    }

    public static LifeCycleType fromCode(String code) {
        for (LifeCycleType type : LifeCycleType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return ELDERLY; // 기본값 설정
    }
} 