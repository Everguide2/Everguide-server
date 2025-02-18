package com.example.everguide.service.mypage;

import com.example.everguide.domain.Member;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.mypage.PasswordUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Member getMypageInfo(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public String updateProfileImage(Long memberId, MultipartFile file) {
        // 파일 업로드 로직 추가 (예: AWS S3 저장 후 URL 반환)
        return "업로드된 파일 URL";
    }

    @Transactional
    public void updatePassword(Long memberId, PasswordUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        memberRepository.save(member);
    }

    @Transactional
    public void deleteAccount(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}