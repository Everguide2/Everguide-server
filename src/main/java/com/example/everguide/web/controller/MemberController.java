package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.jwt.JWTUtil;
import com.example.everguide.redis.RedisLocalTokenRepository;
import com.example.everguide.service.member.MemberCommandService;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.web.dto.MemberRequest;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;

    @PostMapping("/cookie-to-header")
    public ResponseEntity<ApiResponse<String>> cookieToHeader(HttpServletRequest request, HttpServletResponse response) {

        try {
            memberCommandService.cookieToHeader(request, response);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(HttpServletRequest request, HttpServletResponse response) {

        try {
            memberCommandService.reissue(request, response);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
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

    @DeleteMapping("/member/{user_id}")
    public ResponseEntity<ApiResponse<String>> deleteMember(
            @PathVariable(name="user_id") String userId,
            HttpServletRequest request, HttpServletResponse response
    ) {

        try {
            if (memberCommandService.deleteMember(request, response, userId)) {
                return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED));

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "회원 탈퇴에 실패했습니다."));
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure(ErrorStatus._NOT_FOUND, "엔티티를 찾을 수 없습니다."));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }
}
