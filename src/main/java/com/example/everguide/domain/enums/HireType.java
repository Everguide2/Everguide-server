package com.example.everguide.domain.enums;

public enum HireType {
    RECRUITING, COMPLETE; //모집중, 완료

    //xml 데이터를 enum으로 변환하는 메서드
    public static HireType toHireType(String value) {
        if (value == null) {
            return null; // null 그대로 반환
        }
        return switch (value) {
            case "모집중" -> RECRUITING;
            case "완료" -> COMPLETE;
            default -> throw new IllegalArgumentException("Unknown HireTYpe value:" + value);
        };
    }
    }


