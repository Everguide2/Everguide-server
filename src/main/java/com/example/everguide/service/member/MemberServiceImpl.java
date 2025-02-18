package com.example.everguide.service.member;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.ProviderType;
import com.example.everguide.jwt.JWTUtil;
import com.example.everguide.jwt.SecurityUtil;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.service.mail.MailService;
import com.example.everguide.web.dto.member.MemberResponse;
import com.example.everguide.web.dto.auth.CustomOAuth2User;
import com.example.everguide.web.dto.auth.CustomUserDetails;
import com.example.everguide.web.dto.member.MemberRequest;
import com.example.everguide.web.dto.auth.OAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.everguide.repository.MemberRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

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
    private final MailService mailService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtils redisUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JWTUtil jwtUtil;
    private final SecurityUtil securityUtil;

    @Override
    public Boolean checkOriginalPwd(MemberRequest.ChangePwdDTO changePwdDTO) {

        String userId = securityUtil.getCurrentUserId();

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

        return bCryptPasswordEncoder.matches(changePwdDTO.getOriginalPwd(), member.getPassword());
    }

    @Override
    @Transactional
    public boolean changePwd(MemberRequest.ChangePwdDTO changePwdDTO) {

        String userId = securityUtil.getCurrentUserId();
        String newPwd = bCryptPasswordEncoder.encode(changePwdDTO.getNewPwd());

        int success = memberRepository.updatePasswordByUserId(userId, newPwd);

        return success != 0;
    }

    @Override
    @Transactional
    public MemberResponse.FindEmailDTO findEmail(MemberRequest.FindEmailDTO findEmailDTO) {

        String name = findEmailDTO.getName();
        String phoneNumber = findEmailDTO.getPhoneNumber();

        checkSmsVerify(phoneNumber);

        Member member = memberRepository.findByNameAndPhoneNumberAndProviderType(name, phoneNumber, ProviderType.LOCAL).orElse(null);

        String email = null;
        if (member != null) {
            email = member.getEmail();
        }

        return MemberResponse.FindEmailDTO.builder()
                .name(name)
                .email(email)
                .build();
    }

    @Override
    @Transactional
    public Boolean findPwd(MemberRequest.FindPwdDTO findPwdDTO) {

        String email = findPwdDTO.getEmail();
        String name = findPwdDTO.getName();
        String phoneNumber = findPwdDTO.getPhoneNumber();
        String userId = "LOCAL_" + email;

        checkSmsVerify(phoneNumber);

        Member member = memberRepository.findByEmailAndNameAndPhoneNumberAndProviderType(email, name, phoneNumber, ProviderType.LOCAL).orElseThrow(EntityNotFoundException::new);

        String code = mailService.sendEmail(member.getEmail());

        int success = memberRepository.updatePasswordByUserId(userId, bCryptPasswordEncoder.encode(code));

        return success != 0;
    }

    private Boolean checkSmsVerify(String phoneNumber) {

        String savedCode = redisUtils.getSmsAuthCode(phoneNumber);
        String verifyCode = redisUtils.getSmsAuthCodeVerify(phoneNumber);

        if (savedCode == null || verifyCode == null) {
            throw new MemberBadRequestException("인증코드를 찾을 수 없습니다.");
        }

        if (!savedCode.equals(verifyCode)) {
            throw new MemberBadRequestException("인증코드가 일치하지 않습니다.");
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteMember(HttpServletRequest request, HttpServletResponse response, String userId) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            throw new MemberBadRequestException(ErrorStatus._NO_TOKEN.getMessage());
        }

        String accessToken = authorization.split(" ")[1];

        try {
            jwtUtil.isExpired(accessToken);

        } catch (ExpiredJwtException e) {

            throw new MemberBadRequestException(ErrorStatus._TOKEN_EXPIRED.getMessage());
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            throw new MemberBadRequestException("Access Token이 아닙니다.");
        }

        String currentUserId = jwtUtil.getUserId(accessToken);
        String social = jwtUtil.getSocial(accessToken);

        if (!currentUserId.equals(userId)) {
            throw new MemberBadRequestException("현재 로그인한 회원이 아닙니다.");
        }

        boolean memberDeleteSuccess = false;
        if (social.equals("local")) {
            memberDeleteSuccess = deleteLocalMember(userId);
        } else {
            memberDeleteSuccess = deleteSocialMember(userId);
        }

        if (memberDeleteSuccess) {

            // Refresh 토큰 Cookie 값 0
            Cookie cookie = new Cookie("refresh", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);

            return true;

        } else {
            return false;
        }
    }

    private boolean deleteLocalMember(String userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUserId = customUserDetails.getUsername();

        if (!currentUserId.equals(userId)) {
            throw new MemberBadRequestException(ErrorStatus._INVALID_CREDENTIALS.getMessage());
        }

        // Redis에 저장되어 있는지 확인
        String redisRefreshToken = redisUtils.getLocalRefreshToken(userId);
        if (redisRefreshToken == null) {

            throw new MemberBadRequestException(ErrorStatus._LOCAL_INVALID_TOKEN.getMessage());
        }

        // Refresh 토큰 Redis에서 제거
        redisUtils.deleteLocalRefreshToken(userId);

        List<Bookmark> bookmarkList = bookmarkRepository.findByUserId(userId);
        bookmarkRepository.deleteAllInBatch(bookmarkList);

        memberRepository.deleteByUserId(userId);

        return true;
    }

    private boolean deleteSocialMember(String userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String currentUserId = customOAuth2User.getUserId();

        if (!currentUserId.equals(userId)) {
            throw new MemberBadRequestException(ErrorStatus._INVALID_CREDENTIALS.getMessage());
        }

        // Redis에 저장되어 있는지 확인
        String redisRefreshToken = redisUtils.getLocalRefreshToken(userId);
        if (redisRefreshToken == null) {

            throw new MemberBadRequestException(ErrorStatus._LOCAL_INVALID_TOKEN.getMessage());
        }

        // Refresh 토큰 Redis에서 제거
        redisUtils.deleteLocalRefreshToken(userId);

        String socialAccessToken = redisUtils.getSocialAccessToken(userId);
        String socialRefreshToken = redisUtils.getSocialRefreshToken(userId);

        String currentSocial = customOAuth2User.getSocial();

        if (currentSocial.equals("naver")) {
            naverSocialDisconnect(socialAccessToken, socialRefreshToken);

        } else if (currentSocial.equals("kakao")) {
            kakaoSocialDisconnect(userId, socialAccessToken);

        } else {
            throw new MemberBadRequestException(ErrorStatus._INVALID_PROVIDER_TYPE.getMessage());
        }

        redisUtils.deleteSocialTokens(userId);

        List<Bookmark> bookmarkList = bookmarkRepository.findByUserId(userId);
        bookmarkRepository.deleteAllInBatch(bookmarkList);

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

            throw new MemberBadRequestException(ErrorStatus._SOCIAL_INVALID_TOKEN.getMessage());
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

            throw new MemberBadRequestException(ErrorStatus._SOCIAL_INVALID_TOKEN.getMessage());
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

            throw new MemberBadRequestException(ErrorStatus._SOCIAL_INVALID_TOKEN.getMessage());
        }

        return (String) response.getBody();
    }

    @Override
    @Transactional
    public void updateRedis() {

        System.out.println("update Redis");

        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(10).build();
        Cursor<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);

        while (keys.hasNext()) {

            String key = new String(keys.next());

            if (key.startsWith("SocialRefresh:")) {

                if (!key.endsWith(":phantom")) {

                    int indexUnderscore = key.indexOf("_");
                    int indexColon = key.indexOf(":");

                    String provider = key.substring(indexColon+1, indexUnderscore);
                    String userId = key.substring(indexColon+1);

                    renewTokens(provider, userId);
                }
            }
        }
    }

    private void renewTokens(String provider, String userId) {

        String socialRefreshToken = redisUtils.getSocialRefreshToken(userId);

        if (provider.equals("KAKAO")) {
            kakaoRenew(userId, socialRefreshToken);
        } else if (provider.equals("NAVER")) {
            naverRenew(userId, socialRefreshToken);
        }
    }

    private String kakaoRenew(String userId, String socialRefreshToken) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", kakaoClientId);
        params.add("refresh_token", socialRefreshToken);
        params.add("client_secret", kakaoClientSecret);

        HttpEntity<MultiValueMap<String, String>> kakaoRenewRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoRenewRequest,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {

            throw new MemberBadRequestException(ErrorStatus._SOCIAL_INVALID_TOKEN.getMessage());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);

        } catch (JsonProcessingException e) {
            return e.getMessage();
        }

        redisUtils.deleteSocialAccessToken(userId);
        redisUtils.setSocialAccessToken(userId, oAuthToken.getAccessToken(), 1L);
        if (oAuthToken.getRefreshToken() != null) {
            redisUtils.deleteSocialRefreshToken(userId);
            redisUtils.setSocialRefreshToken(userId, oAuthToken.getRefreshToken(), 1L);
        }

        return (String) response.getBody();
    }

    private String naverRenew(String userId, String socialRefreshToken) {

        RestTemplate rt = new RestTemplate();

        String url = "https://nid.naver.com/oauth2.0/token?grant_type=refresh_token&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&refresh_token=" + socialRefreshToken;

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> naverLogoutRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                url,
                HttpMethod.POST,
                naverLogoutRequest,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {

            throw new MemberBadRequestException(ErrorStatus._SOCIAL_INVALID_TOKEN.getMessage());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);

        } catch (JsonProcessingException e) {
            return e.getMessage();
        }

        redisUtils.deleteSocialAccessToken(userId);
        redisUtils.setSocialAccessToken(userId, oAuthToken.getAccessToken(), 1L);

        return (String) response.getBody();
    }
} 