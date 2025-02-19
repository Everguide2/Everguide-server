package com.example.everguide.service.signup;

import com.example.everguide.web.dto.member.MemberResponse;
import com.example.everguide.web.dto.signup.SignupRequest;
import com.example.everguide.web.dto.signup.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SignupService {

    Boolean checkEmailExist(String userId);

    SignupResponse.AdditionalNotValidateDTO checkInfoEqual(HttpServletRequest request, HttpServletResponse response, SignupRequest.SignupAdditionalDTO signupAdditionalDTO);

    boolean localSignUp(SignupRequest.SignupDTO signupDTO);

    SignupResponse.SignupAdditionalDTO getSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response);

    boolean registerSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response, SignupRequest.SignupAdditionalDTO signupAdditionalDTO);
}
