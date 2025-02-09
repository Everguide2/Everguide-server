package com.example.everguide.service.member;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.handler.MemberExceptionHandler;
import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.Gender;
import com.example.everguide.domain.enums.ProviderType;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.service.redis.RedisUtils;
import com.example.everguide.web.dto.oauth.CustomOAuth2User;
import com.example.everguide.web.dto.oauth.CustomUserDetails;
import com.example.everguide.web.dto.MemberRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.everguide.domain.enums.Role;

import com.example.everguide.repository.MemberRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandServiceImpl implements MemberCommandService {
    
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtils redisUtils;

    @Override
    @Transactional
    public boolean localSignUp(MemberRequest.SignupDTO signupDTO) {

        String name = signupDTO.getName();
        String birth = signupDTO.getBirth();
        String gender = signupDTO.getGender();
        String phoneNumber = signupDTO.getPhoneNumber();
        String email = signupDTO.getEmail();
        String password = signupDTO.getPassword();
        String userId = "LOCAL_" + email;

        Boolean isExist = memberRepository.existsByUserId(userId);

        if (isExist) {
            return false;
        }

        Member member = Member.builder()
                .name(name)
                .birth(LocalDate.parse(birth, DateTimeFormatter.BASIC_ISO_DATE))
                .gender(Gender.valueOf(gender))
                .phoneNumber(phoneNumber)
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .role(Role.ROLE_MEMBER)
                .providerType(ProviderType.LOCAL)
                .userId(userId)
                .build();

        memberRepository.save(member);

        return true;
    }

    @Override
    @Transactional
    public boolean deleteLocalMember(Long memberId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUserId = customUserDetails.getUsername();
        Long currentMemberId = memberRepository.findByUserId(currentUserId).orElseThrow(EntityNotFoundException::new).getId();

        if (!currentMemberId.equals(memberId)) {
            throw new MemberExceptionHandler(ErrorStatus._INVALID_CREDENTIALS);
        }

        List<Bookmark> bookmarkList = bookmarkRepository.findByMemberId(memberId);
        bookmarkRepository.deleteAll(bookmarkList);

        memberRepository.deleteById(memberId);

        return true;
    }

    @Override
    @Transactional
    public boolean deleteSocialMember(Long memberId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String currentUserId = customOAuth2User.getUserId();
        Long currentMemberId = memberRepository.findByUserId(currentUserId).orElseThrow(EntityNotFoundException::new).getId();

        if (!currentMemberId.equals(memberId)) {
            throw new MemberExceptionHandler(ErrorStatus._INVALID_CREDENTIALS);
        }

        List<Bookmark> bookmarkList = bookmarkRepository.findByMemberId(memberId);
        bookmarkRepository.deleteAll(bookmarkList);

        memberRepository.deleteById(memberId);

        return true;
    }

    @Override
    @Transactional
    public void changeRefreshToken(String userId, String refreshToken, Long expiredMs) {

        redisUtils.deleteToken(userId);
        redisUtils.setToken(userId, refreshToken, expiredMs);
    }
} 