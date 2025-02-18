package com.example.everguide.service.signup;

import com.example.everguide.web.dto.MemberRequest;
import com.example.everguide.web.dto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SignupService {

    Boolean checkEmailExist(String userId);

    MemberResponse.AdditionalNotValidateDTO checkInfoEqual(HttpServletRequest request, HttpServletResponse response, MemberRequest.SignupAdditionalDTO signupAdditionalDTO);

    boolean localSignUp(MemberRequest.SignupDTO signupDTO);

    MemberResponse.SignupAdditionalDTO getSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response);

    boolean registerSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response, MemberRequest.SignupAdditionalDTO signupAdditionalDTO);
}
