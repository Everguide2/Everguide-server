package com.example.everguide.validation;

import com.example.everguide.web.dto.MemberRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangePasswordValidator implements Validator {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,13}$");

    private final MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberRequest.ChangePwdDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        MemberRequest.ChangePwdDTO changePwdDTO = (MemberRequest.ChangePwdDTO) target;

        // 비밀번호
        if (valueNullOrBlank(changePwdDTO.getNewPwd())) {
            errors.rejectValue("newPwd", "blank.password", getMessage("blank.password"));
        } else if (!PASSWORD_PATTERN.matcher(changePwdDTO.getNewPwd()).matches()) {
            errors.rejectValue("newPwd", "invalid.password", getMessage("invalid.password"));
        } else if (changePwdDTO.getNewPwd().equals(changePwdDTO.getOriginalPwd())) {
            errors.rejectValue("newPwd", "duplicate.password", getMessage("duplicate.password"));
        }

        // 비밀번호 재입력
        if (valueNullOrBlank(changePwdDTO.getRewriteNewPwd())) {
            errors.rejectValue("rewriteNewPwd", "blank.rewritePassword", getMessage("blank.rewritePassword"));
        } else if (!changePwdDTO.getNewPwd().equals(changePwdDTO.getRewriteNewPwd())) {
            errors.rejectValue("rewriteNewPwd", "invalid.rewritePassword", getMessage("invalid.rewritePassword"));
        }
    }

    private boolean valueNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, null);
    }
}
