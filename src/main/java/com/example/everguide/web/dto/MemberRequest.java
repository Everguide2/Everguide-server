package com.example.everguide.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MemberRequest {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupDTO {

        String name;
        String birth;
        String gender;
        String phoneNumber;
        String email;
        String password;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupEmailDTO {

        String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupAdditionalDTO {

        String name;
        String birth;
        String gender;
        String phoneNumber;
        String email;
    }
}
