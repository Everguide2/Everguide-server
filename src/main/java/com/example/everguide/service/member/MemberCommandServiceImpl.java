package com.example.everguide.service.member;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.Gender;
import com.example.everguide.domain.enums.ProviderType;
import com.example.everguide.jwt.JWTUtil;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.web.dto.MemberResponse;
import com.example.everguide.web.dto.oauth.CustomOAuth2User;
import com.example.everguide.web.dto.oauth.CustomUserDetails;
import com.example.everguide.web.dto.MemberRequest;
import com.example.everguide.web.dto.oauth.OAuthToken;
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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.everguide.domain.enums.Role;

import com.example.everguide.repository.MemberRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.PrintWriter;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final JWTUtil jwtUtil;

    @Override
    public Boolean cookieToHeader(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            throw new MemberBadRequestException("Token이 null입니다.");
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            throw new MemberBadRequestException("만료된 토큰입니다.");
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            throw new MemberBadRequestException("Refresh Token이 아닙니다.");
        }

        String userId = jwtUtil.getUserId(refresh);
        String role = jwtUtil.getRole(refresh);
        String social = jwtUtil.getSocial(refresh);

        //make new JWT
        String access = jwtUtil.createJwt(userId, role, social, "access", 60000 * 10L);

        redisUtils.setLocalRefreshToken(access, refresh, 60000 * 60 * 24L);

        //response
        response.setHeader("Authorization", "Bearer " + access);
        response.setStatus(HttpStatus.OK.value());

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

        Boolean full = (member.getName() != null)
                && (member.getBirth() != null)
                && (member.getGender() != null)
                && (member.getPhoneNumber() != null)
                && (member.getEmail() != null);

        return full;
    }

    @Override
    public boolean reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            throw new MemberBadRequestException(ErrorStatus._TOKEN_NULL.getMessage());
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            throw new MemberBadRequestException(ErrorStatus._TOKEN_EXPIRED.getMessage());
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            throw new MemberBadRequestException("Refresh Token이 아닙니다.");
        }

        String userId = jwtUtil.getUserId(refresh);
        String role = jwtUtil.getRole(refresh);
        String social = jwtUtil.getSocial(refresh);

        Boolean isExist = redisUtils.existsLocalRefreshToken(refresh);
        if (!isExist) {

            throw new MemberBadRequestException(ErrorStatus._LOCAL_INVALID_TOKEN.getMessage());
        }

        //make new JWT
        String newAccess = jwtUtil.createJwt(userId, role, social, "access", 60000*10L);

        //response
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.setStatus(HttpStatus.OK.value());

        return true;
    }

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
    public MemberResponse.SignupAdditionalDTO getSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response) {

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

        String userId = jwtUtil.getUserId(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        String gender;
        if (member.getGender() != null) {
            gender = member.getGender().name();
        } else {
            gender = null;
        }

        return MemberResponse.SignupAdditionalDTO.builder()
                .name(member.getName())
                .birth(member.getBirth())
                .gender(gender)
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .build();
    }

    @Override
    @Transactional
    public boolean registerSignupAdditionalInfo(
            HttpServletRequest request, HttpServletResponse response,
            MemberRequest.SignupAdditionalDTO signupAdditionalDTO) {

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

        String userId = jwtUtil.getUserId(accessToken);
        String social = jwtUtil.getSocial(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

        member.setName(signupAdditionalDTO.getName());
        LocalDate birth = LocalDate.parse(signupAdditionalDTO.getBirth(), DateTimeFormatter.BASIC_ISO_DATE);
        member.setBirth(birth);
        member.setGender(Gender.valueOf(signupAdditionalDTO.getGender()));
        member.setPhoneNumber(signupAdditionalDTO.getPhoneNumber());
        member.setEmail(signupAdditionalDTO.getEmail());
        member.setRole(Role.ROLE_MEMBER);

        memberRepository.save(member);

        String access = jwtUtil.createJwt(userId, Role.ROLE_MEMBER.name(), social, "access", 60000*10L);
        String refresh = jwtUtil.createJwt(userId, Role.ROLE_MEMBER.name(), social, "refresh", 60000*60*24L);

        redisUtils.changeLocalRefreshToken(accessToken, access, refresh, 60000*60*24L);

        response.addHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        return true;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
//        cookie.setSecure(true);
//        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
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

        String social = jwtUtil.getSocial(accessToken);

        boolean memberDeleteSuccess = false;
        if (social.equals("local")) {
            memberDeleteSuccess = deleteLocalMember(userId, accessToken);
        } else {
            memberDeleteSuccess = deleteSocialMember(userId, accessToken);
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

    private boolean deleteLocalMember(String userId, String accessToken) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUserId = customUserDetails.getUsername();

        if (!currentUserId.equals(userId)) {
            throw new MemberBadRequestException(ErrorStatus._INVALID_CREDENTIALS.getMessage());
        }

        // Redis에 저장되어 있는지 확인
        String redisRefreshToken = redisUtils.getLocalRefreshToken(accessToken);
        if (redisRefreshToken == null) {

            throw new MemberBadRequestException(ErrorStatus._LOCAL_INVALID_TOKEN.getMessage());
        }

        // Refresh 토큰 Redis에서 제거
        redisUtils.deleteLocalRefreshToken(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        List<Bookmark> bookmarkList = bookmarkRepository.findByMemberId(member.getId());
        bookmarkRepository.deleteAll(bookmarkList);

        memberRepository.deleteByUserId(userId);

        return true;
    }

    private boolean deleteSocialMember(String userId, String accessToken) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String currentUserId = customOAuth2User.getUserId();

        if (!currentUserId.equals(userId)) {
            throw new MemberBadRequestException(ErrorStatus._INVALID_CREDENTIALS.getMessage());
        }

        // Redis에 저장되어 있는지 확인
        String redisRefreshToken = redisUtils.getLocalRefreshToken(accessToken);
        if (redisRefreshToken == null) {

            throw new MemberBadRequestException(ErrorStatus._LOCAL_INVALID_TOKEN.getMessage());
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
            throw new MemberBadRequestException(ErrorStatus._INVALID_PROVIDER_TYPE.getMessage());
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
    public void changeRefreshToken(String accessToken, String refreshToken, Long expiredMs) {

        redisUtils.deleteLocalRefreshToken(accessToken);
        redisUtils.setLocalRefreshToken(accessToken, refreshToken, expiredMs);
    }

    @Override
    @Transactional
    public void updateRedis() {

        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(10).build();
        Cursor<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);

        while (keys.hasNext()) {
            String key = new String(keys.next());
            int index = key.indexOf("_");

            String provider = key.substring(0, index);
//            String providerId = key.substring(index + 1);

            renewTokens(provider, key);
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

        redisUtils.setSocialAccessToken(userId, oAuthToken.getAccessToken(), 1L);
        if (oAuthToken.getRefreshToken() != null) {
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

        redisUtils.setSocialAccessToken(userId, oAuthToken.getAccessToken(), 1L);

        return (String) response.getBody();
    }

    @Override
    @Transactional
    public boolean updateProfile(String userId, MemberRequest.UpdateProfileDTO request) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 이메일 중복 체크 (현재 사용자의 이메일은 제외)
        if (!member.getEmail().equals(request.getEmail())) {
            memberRepository.findByEmail(request.getEmail())
                    .ifPresent(m -> {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                    });
        }

        member.updateProfile(
                request.getName(),
                LocalDate.parse(request.getBirth()),
                request.getPhoneNumber(),
                request.getEmail()
        );

        return true;
    }

    @Override
    @Transactional
    public boolean updatePassword(String userId, MemberRequest.UpdatePasswordDTO request) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 소셜 로그인 회원인 경우 비밀번호 변경 불가
        if (member.getProviderType() != ProviderType.LOCAL) {
            throw new IllegalStateException("소셜 로그인 회원은 비밀번호를 변경할 수 없습니다.");
        }

        // 현재 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 비밀번호 일치 여부 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        return true;
    }
} 