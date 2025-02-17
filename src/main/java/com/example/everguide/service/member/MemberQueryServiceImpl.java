package com.example.everguide.service.member;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.domain.Member;
import com.example.everguide.jwt.JWTUtil;
import com.example.everguide.web.dto.MemberRequest;
import com.example.everguide.web.dto.MemberResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.everguide.repository.MemberRepository;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {
    
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public Map<String, String> validateHandling(Errors errors) {

        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {

            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }

        return validatorResult;
    }

    @Override
    public Boolean checkEmailExist(String userId) {

        Member member = memberRepository.findByUserId(userId).orElse(null);

        return member == null;
    }

    @Override
    public MemberResponse.AdditionalNotValidateDTO checkInfoEqual(HttpServletRequest request, HttpServletResponse response,
                           MemberRequest.SignupAdditionalDTO signupAdditionalDTO) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            throw new MemberBadRequestException(ErrorStatus._NO_TOKEN.getMessage());
        }

        String accessToken = authorization.split(" ")[1];

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            throw new MemberBadRequestException(ErrorStatus._TOKEN_EXPIRED.getMessage());
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            throw new MemberBadRequestException("Access Token이 아닙니다.");
        }

        String userId = jwtUtil.getUserId(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        Boolean infoEqual = true;
        String checkName;
        LocalDate checkBirth;
        String checkPhoneNumber;
        String checkEmail;

        if (member.getName() != null) {
            if (!member.getName().equals(signupAdditionalDTO.getName())) {
                infoEqual = false;
            }
            checkName = member.getName();
        } else {
            checkName = null;
        }

        if (member.getBirth() != null) {
            String birth = member.getBirth().format(DateTimeFormatter.BASIC_ISO_DATE);
            if (!birth.equals(signupAdditionalDTO.getBirth())) {
                infoEqual = false;
            }
            checkBirth = member.getBirth();
        } else {
            checkBirth = null;
        }

        if (member.getPhoneNumber() != null) {
            if (!member.getPhoneNumber().equals(signupAdditionalDTO.getPhoneNumber())) {
                infoEqual = false;
            }
            checkPhoneNumber = member.getPhoneNumber();
        } else {
            checkPhoneNumber = null;
        }

        if (member.getEmail() != null) {
            if (!member.getEmail().equals(signupAdditionalDTO.getEmail())) {
                infoEqual = false;
            }
            checkEmail = member.getEmail();
        } else {
            checkEmail = null;
        }

        if (infoEqual) {
            return null;

        } else {
            return MemberResponse.AdditionalNotValidateDTO.builder()
                    .name(checkName)
                    .birth(checkBirth)
                    .phoneNumber(checkPhoneNumber)
                    .email(checkEmail)
                    .validatorResult(null)
                    .build();
        }
    }

    @Override
    public Boolean checkOriginalPwd(HttpServletRequest request, HttpServletResponse response, MemberRequest.ChangePwdDTO changePwdDTO) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            throw new MemberBadRequestException(ErrorStatus._NO_TOKEN.getMessage());
        }

        String accessToken = authorization.split(" ")[1];

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            throw new MemberBadRequestException(ErrorStatus._TOKEN_EXPIRED.getMessage());
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            throw new MemberBadRequestException("Access Token이 아닙니다.");
        }

        String userId = jwtUtil.getUserId(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

        return bCryptPasswordEncoder.matches(changePwdDTO.getOriginalPwd(), member.getPassword());
    }
} 