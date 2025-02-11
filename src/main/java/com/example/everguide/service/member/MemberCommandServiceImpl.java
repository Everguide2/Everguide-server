package com.example.everguide.service.member;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.handler.MemberExceptionHandler;
import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.Gender;
import com.example.everguide.domain.enums.ProviderType;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.web.dto.oauth.CustomOAuth2User;
import com.example.everguide.web.dto.oauth.CustomUserDetails;
import com.example.everguide.web.dto.MemberRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.everguide.domain.enums.Role;

import com.example.everguide.repository.MemberRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandServiceImpl implements MemberCommandService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

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
    public boolean deleteLocalMember(String userId, String accessToken) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUserId = customUserDetails.getUsername();

        if (!currentUserId.equals(userId)) {
            throw new MemberExceptionHandler(ErrorStatus._INVALID_CREDENTIALS);
        }

        // Redis에 저장되어 있는지 확인
        String redisRefreshToken = redisUtils.getLocalRefreshToken(accessToken);
        if (redisRefreshToken == null) {

            throw new MemberExceptionHandler(ErrorStatus._INVALID_TOKEN);
        }

        // Refresh 토큰 Redis에서 제거
        redisUtils.deleteLocalRefreshToken(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        List<Bookmark> bookmarkList = bookmarkRepository.findByMemberId(member.getId());
        bookmarkRepository.deleteAll(bookmarkList);

        memberRepository.deleteByUserId(userId);

        return true;
    }

    @Override
    @Transactional
    public boolean deleteSocialMember(String userId, String accessToken) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String currentUserId = customOAuth2User.getUserId();

        if (!currentUserId.equals(userId)) {
            throw new MemberExceptionHandler(ErrorStatus._INVALID_CREDENTIALS);
        }

        // Redis에 저장되어 있는지 확인
        String redisRefreshToken = redisUtils.getLocalRefreshToken(accessToken);
        if (redisRefreshToken == null) {

            throw new MemberExceptionHandler(ErrorStatus._INVALID_TOKEN);
        }

        // Refresh 토큰 Redis에서 제거
        redisUtils.deleteLocalRefreshToken(accessToken);

        String socialAccessToken = redisUtils.getSocialAccessToken(userId);
        String socialRefreshToken = redisUtils.getSocialRefreshToken(userId);

        String currentSocial = customOAuth2User.getSocial();

        if (currentSocial.equals("naver")) {
            naverSocialDisconnect(socialAccessToken, socialRefreshToken);

        } else if (currentSocial.equals("kakao")) {
            kakaoSocialDisconnect(userId, socialAccessToken);

        } else {
            throw new MemberExceptionHandler(ErrorStatus._INVALID_PROVIDER_TYPE);
        }

        redisUtils.deleteSocialTokens(userId);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        List<Bookmark> bookmarkList = bookmarkRepository.findByMemberId(member.getId());
        bookmarkRepository.deleteAll(bookmarkList);

        memberRepository.deleteByUserId(userId);

        return true;
    }

    private String naverSocialDisconnect(String socialAccessToken, String socialRefreshToken) {

        RestTemplate rtRenew = new RestTemplate();

        String urlRenew = "https://nid.naver.com/oauth2.0/token?grant_type=refresh_token&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&refresh_token=" + socialRefreshToken;

        HttpHeaders headersRenew = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> naverRenewRequest = new HttpEntity<>(headersRenew);

        ResponseEntity<String> responseRenew = rtRenew.exchange(
                urlRenew,
                HttpMethod.POST,
                naverRenewRequest,
                String.class
        );

        if (responseRenew.getStatusCode() != HttpStatus.OK) {

            throw new MemberExceptionHandler(ErrorStatus._INVALID_TOKEN);
        }

        RestTemplate rt = new RestTemplate();

        String url = "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&access_token=" + socialAccessToken;

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> naverDisconnectRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                url,
                HttpMethod.POST,
                naverDisconnectRequest,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {

            throw new MemberExceptionHandler(ErrorStatus._INVALID_TOKEN);
        }

        return (String) response.getBody();
    }

    private String kakaoSocialDisconnect(String userId, String socialAccessToken) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + socialAccessToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", userId.substring(6));

        HttpEntity<MultiValueMap<String, String>> kakaoDisconnectRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/unlink",
                HttpMethod.POST,
                kakaoDisconnectRequest,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {

            throw new MemberExceptionHandler(ErrorStatus._INVALID_TOKEN);
        }

        return (String) response.getBody();
    }

    @Override
    @Transactional
    public void changeRefreshToken(String accessToken, String refreshToken, Long expiredMs) {

        redisUtils.deleteLocalRefreshToken(accessToken);
        redisUtils.setLocalRefreshToken(accessToken, refreshToken, expiredMs);
    }
} 