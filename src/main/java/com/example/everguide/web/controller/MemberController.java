package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.jwt.JWTUtil;
import com.example.everguide.redis.RedisTokenRepository;
import com.example.everguide.service.member.MemberCommandService;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.web.dto.MemberRequest;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final JWTUtil jwtUtil;
    private final RedisUtils redisUtils;
    private final RedisTokenRepository redisTokenRepository;

    public MemberController(MemberCommandService memberCommandService, JWTUtil jwtUtil, RedisUtils redisUtils, RedisTokenRepository redisTokenRepository) {
        this.memberCommandService = memberCommandService;
        this.jwtUtil = jwtUtil;
        this.redisUtils = redisUtils;
        this.redisTokenRepository = redisTokenRepository;
    }

    @PostMapping("/cookie-to-header")
    public ResponseEntity<ApiResponse<String>> cookieToHeader(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "authorization token null", null));
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "authorization token expired", null));
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "invalid refresh token", null));
        }

        String userId = jwtUtil.getUserId(refresh);
        String role = jwtUtil.getRole(refresh);
        String social = jwtUtil.getSocial(refresh);

        //make new JWT
        String access = jwtUtil.createJwt(userId, role, social, "access", 60000*10L);

        redisUtils.setLocalRefreshToken(access, refresh, 60000*60*24L);

        //response
        response.setHeader("Authorization", "Bearer " + access);
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "refresh token null", null));
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "refresh token expired", null));
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "invalid refresh token", null));
        }

        String userId = jwtUtil.getUserId(refresh);
        String role = jwtUtil.getRole(refresh);
        String social = jwtUtil.getSocial(refresh);

        Boolean isExist = redisUtils.existsLocalRefreshToken(refresh);
        if (!isExist) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "invalid refresh token", null));
        }

        //make new JWT
        String newAccess = jwtUtil.createJwt(userId, role, social, "access", 60000*10L);

        //response
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.setStatus(HttpStatus.OK.value());

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));

    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> localSignup(@RequestBody MemberRequest.SignupDTO signupDTO) {

        if (memberCommandService.localSignUp(signupDTO)) {
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("500", "이미 존재하는 회원입니다.", null));
        }
    }

    @DeleteMapping("/member/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMember(
            @PathVariable(name="id") Long memberId,
            HttpServletRequest request, HttpServletResponse response
    ) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "invalid access token", null));
        }

        String accessToken = authorization.split(" ")[1];

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "access token expired", null));
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("500", "not refresh token", null));
        }

        String social = jwtUtil.getSocial(accessToken);

        try {

            boolean memberDeleteSuccess = false;
            if (social.equals("local")) {
                memberDeleteSuccess = memberCommandService.deleteLocalMember(memberId);
            } else {
                memberDeleteSuccess = memberCommandService.deleteSocialMember(memberId);
            }

            if (memberDeleteSuccess) {
                return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.onFailure("500", "회원 탈퇴에 실패했습니다.", null));
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("404", "엔티티를 찾을 수 없습니다.", null));
        }
    }
}
