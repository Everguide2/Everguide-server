package com.example.everguide.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MemberResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupDTO {

        String name;
        String birth;
        String phoneNumber;
        String email;
        String password;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupNotValidateDTO {

        String name;
        String birth;
        String phoneNumber;
        String email;
        String password;
        Map<String, String> validatorResult;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupAdditionalDTO {

        String name;
        LocalDate birth;
        String phoneNumber;
        String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalNotValidateDTO {

        String name;
        LocalDate birth;
        String phoneNumber;
        String email;
        Map<String, String> validatorResult;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindEmailDTO {

        String name;
        List<String> emailList;
    }
}
