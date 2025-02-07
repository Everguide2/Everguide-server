package com.example.everguide.domain.enums;
public enum JobType {
    //int = 인턴형, trn = 연수형 , all = 모두 모집
    INT, TRN, ALL;

    //xml 데이터를 enum으로 변환하는 메서드
    public static JobType toJobType(String value) {
        if (value == null) return null; // null 그대로 반환

        return switch (value) {
            case "int" -> INT;
            case "trn" -> TRN;
            case "all" -> ALL;
            default -> throw new IllegalArgumentException("Unknown HireTYpe value:" + value);
        };
    }
}
