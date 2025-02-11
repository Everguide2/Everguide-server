package com.example.everguide.service.member;

import com.example.everguide.web.dto.MemberRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberCommandService {

    boolean cookieToHeader(HttpServletRequest request, HttpServletResponse response);

    boolean reissue(HttpServletRequest request, HttpServletResponse response);

    boolean localSignUp(MemberRequest.SignupDTO signupDTO);

    boolean deleteMember(HttpServletRequest request, HttpServletResponse response, String userId);

    void changeRefreshToken(String userId, String refreshToken, Long expiredMs);

    void updateRedis();
}