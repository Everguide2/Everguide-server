package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.service.member.MemberCommandService;
import com.example.everguide.web.dto.MemberRequest;
import com.example.everguide.web.dto.MemberResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;

    @GetMapping("/member")
    public String memberP() {

        return "Member Test Controller";
    }

    @GetMapping("/signup/test")
    public String signupTest() {

        return "Signup Test Controller";
    }

    @GetMapping("/noauth")
    public ResponseEntity<ApiResponse<Map<String, String>>> noAuth() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED, "unauthorized"));
    }

    @PostMapping("/cookie-to-header")
    public ResponseEntity<ApiResponse<Boolean>> cookieToHeader(HttpServletRequest request, HttpServletResponse response) {

        try {
            Boolean full = memberCommandService.cookieToHeader(request, response);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK, full));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure(ErrorStatus._NOT_FOUND, "엔티티를 찾을 수 없습니다."));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(HttpServletRequest request, HttpServletResponse response) {

        try {
            memberCommandService.reissue(request, response);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> localSignup(@RequestBody MemberRequest.SignupDTO signupDTO) {

        if (memberCommandService.localSignUp(signupDTO)) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK));

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, "이미 존재하는 회원입니다."));
        }
    }

    @GetMapping("/signup/additional-info")
    public ResponseEntity<ApiResponse<MemberResponse.SignupAdditionalDTO>> getSignupAdditionalInfo(
            HttpServletRequest request, HttpServletResponse response
    ) {

        try {
            MemberResponse.SignupAdditionalDTO signupAdditionalDTO = memberCommandService.getSignupAdditionalInfo(request, response);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK, signupAdditionalDTO));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure(ErrorStatus._NOT_FOUND, "엔티티를 찾을 수 없습니다."));

        }
    }

    @PostMapping("/signup/additional-info")
    public ResponseEntity<ApiResponse<Long>> registerSignupAdditionalInfo(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody MemberRequest.SignupAdditionalDTO signupAdditionalDTO
    ) {

        try {
            if (memberCommandService.registerSignupAdditionalInfo(request, response, signupAdditionalDTO)) {

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.onSuccess(SuccessStatus._OK));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure(ErrorStatus._NOT_FOUND, "엔티티를 찾을 수 없습니다."));

        }
    }

    @DeleteMapping("/member/{user_id}")
    public ResponseEntity<ApiResponse<String>> deleteMember(
            @RequestParam(name="user_id") String userId,
            HttpServletRequest request, HttpServletResponse response
    ) {

        try {
            if (memberCommandService.deleteMember(request, response, userId)) {

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.onSuccess(SuccessStatus._OK));

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, "회원 탈퇴에 실패했습니다."));
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
