package com.example.everguide.service.member;

import com.example.everguide.web.dto.member.MemberRequest;
import com.example.everguide.web.dto.member.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {

    Boolean checkOriginalPwd(MemberRequest.ChangePwdDTO changePwdDTO);

    boolean changePwd(MemberRequest.ChangePwdDTO changePwdDTO);

    boolean deleteMember(HttpServletRequest request, HttpServletResponse response, String userId);

    MemberResponse.FindEmailDTO findEmail(MemberRequest.FindEmailDTO findEmailDTO);

    Boolean findPwd(MemberRequest.FindPwdDTO findPwdDTO);

    void updateRedis();
}