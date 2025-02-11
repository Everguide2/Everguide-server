package com.example.everguide.service.member;

import com.example.everguide.web.dto.MemberRequest;

public interface MemberCommandService {

    boolean localSignUp(MemberRequest.SignupDTO signupDTO);

    boolean deleteLocalMember(String userId, String accessToken);

    boolean deleteSocialMember(String userId, String accessToken);

    void changeRefreshToken(String userId, String refreshToken, Long expiredMs);
}