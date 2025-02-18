package com.example.everguide.service.token;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.domain.enums.Role;
import com.example.everguide.jwt.JWTUtil;
import com.example.everguide.redis.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {

    private final JWTUtil jwtUtil;
    private final RedisUtils redisUtils;

    @Override
    @Transactional
    public Boolean cookieToHeader(HttpServletRequest request, HttpServletResponse response) {

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

        // Redis에 저장되어 있는지 확인
        String currentLocalRefresh = redisUtils.getLocalRefreshToken(userId);
        if (!refresh.equals(currentLocalRefresh)) {

            throw new MemberBadRequestException(ErrorStatus._LOCAL_INVALID_TOKEN.getMessage());
        }

        //make new JWT
        String access = jwtUtil.createJwt(userId, role, social, "access", 60000*10L);

        //response
        response.setHeader("Authorization", "Bearer " + access);
        response.setStatus(HttpStatus.OK.value());


        return Role.getRole(role).equals(Role.ROLE_MEMBER);
    }

    @Override
    @Transactional
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

        // Redis에 저장되어 있는지 확인
        String currentLocalRefresh = redisUtils.getLocalRefreshToken(userId);
        if (!refresh.equals(currentLocalRefresh)) {

            throw new MemberBadRequestException(ErrorStatus._LOCAL_INVALID_TOKEN.getMessage());
        }

        //make new JWT
        String newAccess = jwtUtil.createJwt(userId, role, social, "access", 60000*10L);
        String newRefresh = jwtUtil.createJwt(userId, role, social, "refresh", 60000*60*24L);

        redisUtils.changeLocalRefreshToken(userId, newRefresh, 60000*60*24L);

        //response
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
        response.setStatus(HttpStatus.OK.value());

        return true;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
