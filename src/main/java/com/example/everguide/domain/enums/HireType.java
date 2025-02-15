package com.example.everguide.domain.enums;

public enum HireType {
    //모집중, 마감
    RECRUITING, COMPLETE;

    //xml 데이터를 enum으로 변환하는 메서드
    //마감이면 COMPLETE 반환, 그 외에는 모집중으로 반환
    public static HireType toHireType(String value) {
        if (value == null) return null; // null 그대로 반환
        if (value.equals("마감")) {
            return COMPLETE;
        } else {
            return RECRUITING;
        }

    }
    }


