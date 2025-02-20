package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.service.token.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    // 소셜 로그인 Refresh 토큰 쿠키 발급 후 Access 토큰 헤더 발급 위함
    @Operation(summary = "소셜 로그인 Refresh 토큰 쿠키 발급 후 Access 토큰 헤더로 반환", description = "소셜 로그인 Refresh 토큰 쿠키 발급 후 Access 토큰을 헤더로 반환합니다.")
    @PostMapping("/cookie-to-header")
    public ResponseEntity<ApiResponse<String>> cookieToHeader(HttpServletRequest request, HttpServletResponse response) {

        try {
            Boolean isMember = tokenService.cookieToHeader(request, response);

            if (isMember) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.onSuccess(SuccessStatus._OK));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.onFailure(ErrorStatus._UNFULL_ADDITIONAL_INFO));
            }

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure(ErrorStatus._NOT_FOUND, "엔티티를 찾을 수 없습니다."));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }

    // Access 토큰 만료 시 재발급
    @Operation(summary = "Access 토큰 만료 시 Access 토큰, Refresh 토큰 재발급", description = "Access 토큰 만료 시 재발급해 Access 토큰은 헤더로 Refresh 토큰은 쿠키로 반환합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(HttpServletRequest request, HttpServletResponse response) {

        try {
            tokenService.reissue(request, response);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }
}
