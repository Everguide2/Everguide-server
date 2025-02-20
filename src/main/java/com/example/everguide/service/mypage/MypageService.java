package com.example.everguide.service.mypage;

import com.example.everguide.domain.Member;
import com.example.everguide.jwt.SecurityUtil;
import com.example.everguide.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public Member getMypageInfo() {
        String userId = securityUtil.getCurrentUserId();
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public String updateProfileImage(MultipartFile file) {
        String userId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 파일 업로드 로직 추가 (예: AWS S3 저장 후 URL 반환)
        return "업로드된 파일 URL";
    }
}
