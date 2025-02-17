package com.example.everguide.service.member;

import com.example.everguide.web.dto.MemberRequest;
import com.example.everguide.web.dto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface MemberCommandService {

    Boolean cookieToHeader(HttpServletRequest request, HttpServletResponse response);

    boolean reissue(HttpServletRequest request, HttpServletResponse response);

    boolean localSignUp(MemberRequest.SignupDTO signupDTO);

    MemberResponse.SignupAdditionalDTO getSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response);

    boolean registerSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response, MemberRequest.SignupAdditionalDTO signupAdditionalDTO);

    boolean changePwd(HttpServletRequest request, HttpServletResponse response, MemberRequest.ChangePwdDTO changePwdDTO);

    boolean deleteMember(HttpServletRequest request, HttpServletResponse response, String userId);

    MemberResponse.FindEmailDTO findEmail(MemberRequest.FindEmailDTO findEmailDTO);

    Boolean findPwd(MemberRequest.FindPwdDTO findPwdDTO);

    void updateRedis();

    boolean updateProfile(String userId, MemberRequest.UpdateProfileDTO request);
    
    boolean updatePassword(String userId, MemberRequest.UpdatePasswordDTO request);
}