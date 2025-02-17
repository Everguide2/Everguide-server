package com.example.everguide.jwt;

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

        String authorization = request.getHeader("Authorization");

        // 토큰이 없다면 다음 필터로 넘김
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            log.info("token null");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료 (필수)
            return;
        }

        // Bearer 부분 제거 후 순수 토큰만 획득
        String accessToken = authorization.split(" ")[1];

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            // response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            // response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String userId = jwtUtil.getUserId(accessToken);
        String social = jwtUtil.getSocial(accessToken);

        // Redis에 저장되어 있는지 확인
        String redisRefreshToken = redisUtils.getLocalRefreshToken(userId);
        if (redisRefreshToken == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
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
