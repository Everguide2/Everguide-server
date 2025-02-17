package com.example.everguide.validation;

import com.example.everguide.web.dto.MemberRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterValidator implements Validator {

    private static final Pattern BIRTH_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^010\\d{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,13}$");

    private final MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberRequest.SignupDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        MemberRequest.SignupDTO signupDTO = (MemberRequest.SignupDTO) target;

        // 이름
        if (valueNullOrBlank(signupDTO.getName())) {
            errors.rejectValue("name", "blank.name", getMessage("blank.name"));
        }

        // 생년월일
        if (signupDTO.getBirth() == null) {
            errors.rejectValue("birth", "blank.birth", getMessage("blank.birth"));
        } else if (!BIRTH_PATTERN.matcher(signupDTO.getBirth()).matches()) {
            errors.rejectValue("birth", "invalid.birth", getMessage("invalid.birth"));
        } else if (!ageValid(LocalDate.parse(signupDTO.getBirth(), DateTimeFormatter.BASIC_ISO_DATE))) {
            errors.rejectValue("birth", "range.birth", getMessage("range.birth"));
        }

        // 전화번호
        if (valueNullOrBlank(signupDTO.getPhoneNumber())) {
            errors.rejectValue("phoneNumber", "blank.phoneNumber", getMessage("blank.phoneNumber"));
        } else if (!PHONE_NUMBER_PATTERN.matcher(signupDTO.getPhoneNumber()).matches()) {
            errors.rejectValue("phoneNumber", "invalid.phoneNumber", getMessage("invalid.phoneNumber"));
        }

        // 이메일
        if (valueNullOrBlank(signupDTO.getEmail())) {
            errors.rejectValue("email", "blank.email", getMessage("blank.email"));
        } else if (!EMAIL_PATTERN.matcher(signupDTO.getEmail()).matches()) {
            errors.rejectValue("email", "invalid.email", getMessage("invalid.email"));
        }

        // 비밀번호
        if (valueNullOrBlank(signupDTO.getPassword())) {
            errors.rejectValue("password", "blank.password", getMessage("blank.password"));
        } else if (!PASSWORD_PATTERN.matcher(signupDTO.getPassword()).matches()) {
            errors.rejectValue("password", "invalid.password", getMessage("invalid.password"));
        }

        // 비밀번호 재입력
        if (valueNullOrBlank(signupDTO.getRewritePassword())) {
            errors.rejectValue("rewritePassword", "blank.rewritePassword", getMessage("blank.rewritePassword"));
        } else if (!signupDTO.getPassword().equals(signupDTO.getRewritePassword())) {
            errors.rejectValue("rewritePassword", "invalid.rewritePassword", getMessage("invalid.rewritePassword"));
        }
    }

    private boolean ageValid(LocalDate birthDate) {

        LocalDate today = LocalDate.now();

        int years = Period.between(birthDate, today).getYears();
        int months = Period.between(birthDate, today).getMonths();
        int days = Period.between(birthDate, today).getDays();

        return !((years < 0) || (months < 0) || (days < 0));
    }

    private boolean valueNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, null);
    }
}
