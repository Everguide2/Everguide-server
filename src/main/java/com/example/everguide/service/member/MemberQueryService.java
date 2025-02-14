package com.example.everguide.service.member;

import com.example.everguide.web.dto.MemberResponse;

public interface MemberQueryService {
    MemberResponse.ProfileDTO getProfile(String userId);
} 