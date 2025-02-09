package com.example.everguide.domain.enums;

public enum Role {
    ROLE_MEMBER;

    public static Role getRole(String roleStr) {
        return switch (roleStr) {
            case "ROLE_MEMBER" -> Role.ROLE_MEMBER;
            default -> throw new IllegalArgumentException("Role을 확인할 수 없습니다 : " + roleStr);
        };
    }
}
