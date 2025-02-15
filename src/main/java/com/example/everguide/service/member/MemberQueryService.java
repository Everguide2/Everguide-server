package com.example.everguide.service.member;

import com.example.everguide.domain.Member;
import com.example.everguide.web.dto.MemberRequest;
import com.example.everguide.web.dto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.Errors;

import java.util.Map;

public interface MemberQueryService {

    Map<String, String> validateHandling(Errors errors);

    Boolean checkEmailExist(String userId);

    MemberResponse.AdditionalNotValidateDTO checkInfoEqual(HttpServletRequest request, HttpServletResponse response, MemberRequest.SignupAdditionalDTO signupAdditionalDTO);

    Boolean checkOriginalPwd(HttpServletRequest request, HttpServletResponse response, MemberRequest.ChangePwdDTO changePwdDTO);
}