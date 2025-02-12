package com.example.everguide.service.member;

import com.example.everguide.web.dto.MemberRequest;
import com.example.everguide.web.dto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public interface MemberCommandService {

    Boolean cookieToHeader(HttpServletRequest request, HttpServletResponse response);

    boolean reissue(HttpServletRequest request, HttpServletResponse response);

    boolean localSignUp(MemberRequest.SignupDTO signupDTO);

    MemberResponse.SignupAdditionalDTO getSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response);

    boolean registerSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response, MemberRequest.SignupAdditionalDTO signupAdditionalDTO);

    boolean deleteMember(HttpServletRequest request, HttpServletResponse response, String userId);

    void changeRefreshToken(String userId, String refreshToken, Long expiredMs);

    void updateRedis();
}