package com.example.everguide.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePwdDTO {

        String originalPwd;
        String newPwd;
        String rewriteNewPwd;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindEmailDTO {

        String name;
        String phoneNumber;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindPwdDTO {

        String email;
        String name;
        String phoneNumber;
    }
}
