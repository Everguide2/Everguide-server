package com.example.everguide.web.dto;

import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.Gender;
import com.example.everguide.domain.enums.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberResponse {

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
    public static class SignupAdditionalDTO {

        String name;
        LocalDate birth;
        String gender;
        String phoneNumber;
        String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileDTO {
        private String name;
        private String birth;
        private String phoneNumber;
        private String email;
        private ProviderType providerType;

        public static ProfileDTO from(Member member) {
            return ProfileDTO.builder()
                    .name(member.getName())
                    .birth(member.getBirth().format(DateTimeFormatter.ISO_DATE))
                    .phoneNumber(member.getPhoneNumber())
                    .email(member.getEmail())
                    .providerType(member.getProviderType())
                    .build();
        }
    }
}
