package com.example.everguide.service.member;

import com.example.everguide.web.dto.MemberRequest;

public interface MemberCommandService {

    boolean localSignUp(MemberRequest.SignupDTO signupDTO);

    boolean deleteLocalMember(Long memberId);

    boolean deleteSocialMember(Long memberId);

    void changeRefreshToken(String userId, String refreshToken, Long expiredMs);
}