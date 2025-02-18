package com.example.everguide.jwt;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.redis.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    private final JWTUtil jwtUtil;
    private final RedisUtils redisUtils;

    public CustomLogoutFilter(JWTUtil jwtUtil, RedisUtils redisUtils) {

        this.jwtUtil = jwtUtil;
        this.redisUtils = redisUtils;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            jwtUtil.isExpired(refresh);

        } catch (ExpiredJwtException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String userId = jwtUtil.getUserId(refresh);
        String social = jwtUtil.getSocial(refresh);

        // Redis에 저장되어 있는지 확인
        String currentLocalRefresh = redisUtils.getLocalRefreshToken(userId);
        if (!refresh.equals(currentLocalRefresh)) {

            throw new MemberBadRequestException(ErrorStatus._LOCAL_INVALID_TOKEN.getMessage());
        }

        // 로그아웃 진행
        // Refresh 토큰 Redis에서 제거
        redisUtils.deleteLocalRefreshToken(userId);

        String socialAccessToken = redisUtils.getSocialAccessToken(userId);

        if (social.equals("kakao")) {
            kakaoSocialLogout(userId, socialAccessToken);
            redisUtils.deleteSocialTokens(userId);

        } else if (social.equals("naver")) {
            naverSocialLogout(socialAccessToken);
            redisUtils.deleteSocialTokens(userId);
        }

        // Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private String kakaoSocialLogout(String userId, String socialAccessToken) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + socialAccessToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", userId.substring(7));

        HttpEntity<MultiValueMap<String, String>> kakaoLogoutRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/logout",
                HttpMethod.POST,
                kakaoLogoutRequest,
                String.class
        );

        return (String) response.getBody();
    }

    private String naverSocialLogout(String socialAccessToken) {

        RestTemplate rt = new RestTemplate();

        String url = "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&access_token=" + socialAccessToken
                + "&service_provider=NAVER";

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> naverLogoutRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                url,
                HttpMethod.POST,
                naverLogoutRequest,
                String.class
        );

        return (String) response.getBody();
    }
}
