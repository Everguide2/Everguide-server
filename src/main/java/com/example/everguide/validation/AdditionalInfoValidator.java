package com.example.everguide.validation;

import com.example.everguide.web.dto.signup.SignupRequest;
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
public class AdditionalInfoValidator implements Validator {

    private static final Pattern BIRTH_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^010\\d{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$");

    private final MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return SignupRequest.SignupAdditionalDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        SignupRequest.SignupAdditionalDTO signupAdditionalDTO = (SignupRequest.SignupAdditionalDTO) target;

        // 이름
        if (valueNullOrBlank(signupAdditionalDTO.getName())) {
            errors.rejectValue("name", "blank.name", getMessage("blank.name"));
        }

        // 생년월일
        if (signupAdditionalDTO.getBirth() == null) {
            errors.rejectValue("birth", "blank.birth", getMessage("blank.birth"));
        } else if (!BIRTH_PATTERN.matcher(signupAdditionalDTO.getBirth()).matches()) {
            errors.rejectValue("birth", "invalid.birth", getMessage("invalid.birth"));
        } else if (!ageValid(LocalDate.parse(signupAdditionalDTO.getBirth(), DateTimeFormatter.BASIC_ISO_DATE))) {
            errors.rejectValue("birth", "range.birth", getMessage("range.birth"));
        }

        // 전화번호
        if (valueNullOrBlank(signupAdditionalDTO.getPhoneNumber())) {
            errors.rejectValue("phoneNumber", "blank.phoneNumber", getMessage("blank.phoneNumber"));
        } else if (!PHONE_NUMBER_PATTERN.matcher(signupAdditionalDTO.getPhoneNumber()).matches()) {
            errors.rejectValue("phoneNumber", "invalid.phoneNumber", getMessage("invalid.phoneNumber"));
        }

        // 이메일
        if (valueNullOrBlank(signupAdditionalDTO.getEmail())) {
            errors.rejectValue("email", "blank.email", getMessage("blank.email"));
        } else if (!EMAIL_PATTERN.matcher(signupAdditionalDTO.getEmail()).matches()) {
            errors.rejectValue("email", "invalid.email", getMessage("invalid.email"));
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
