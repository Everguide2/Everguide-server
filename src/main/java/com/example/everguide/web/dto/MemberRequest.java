package com.example.everguide.web.dto;

import jakarta.validation.constraints.*;
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
    public static class SignupAdditionalDTO {

        String name;
        String birth;
        String gender;
        String phoneNumber;
        String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileDTO {
        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
        private String name;

        @NotNull(message = "생년월일은 필수 입력값입니다.")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$", message = "생년월일은 YYYY-MM-DD 형식으로 입력해주세요.")
        private String birth;

        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다.")
        private String phoneNumber;

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 50, message = "이메일은 50자를 초과할 수 없습니다.")
        private String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePasswordDTO {
        @NotBlank(message = "기존 비밀번호를 작성해주세요")
        private String currentPassword;

        @NotBlank(message = "비밀번호를 작성해주세요")
        @Size(min = 8, message = "8자 이상으로 입력")
        @Pattern(regexp = ".*[A-Za-z].*", message = "영어 사용")
        @Pattern(regexp = ".*\\d.*", message = "숫자 사용")
        @Pattern(regexp = ".*[@$!%*#?&].*", message = "특수문자 사용")
        private String newPassword;

        @NotBlank(message = "비밀번호를 다시 한 번 입력해주세요")
        private String confirmPassword;
    }
}
