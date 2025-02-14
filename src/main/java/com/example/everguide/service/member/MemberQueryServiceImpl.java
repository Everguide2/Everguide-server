package com.example.everguide.service.member;

import com.example.everguide.domain.Member;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;

    @Override
    public MemberResponse.ProfileDTO getProfile(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        
        return MemberResponse.ProfileDTO.from(member);
    }
} 