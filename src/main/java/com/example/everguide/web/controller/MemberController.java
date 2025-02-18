package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.service.member.MemberService;
import com.example.everguide.service.validate.ValidateService;
import com.example.everguide.validation.ChangePasswordValidator;
import com.example.everguide.web.dto.MemberRequest;
import com.example.everguide.web.dto.MemberResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ValidateService validateService;

    private final ChangePasswordValidator changePasswordValidator;

    // 이메일 찾기
    @PostMapping("/find-email")
    public ResponseEntity<ApiResponse<MemberResponse.FindEmailDTO>> findEmail(
            @RequestBody MemberRequest.FindEmailDTO findEmailDTO) {

        try {
            MemberResponse.FindEmailDTO findEmailResponseDTO = memberService.findEmail(findEmailDTO);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK, findEmailResponseDTO));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }

    // 비밀번호 찾기
    @PostMapping("/find-pwd")
    public ResponseEntity<ApiResponse<String>> findEmail(
            @RequestBody MemberRequest.FindPwdDTO findPwdDTO) {

        try {
            if (memberService.findPwd(findPwdDTO)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.onSuccess(SuccessStatus._OK));

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, "비밀번호 전송에 실패했습니다."));
            }

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure(ErrorStatus._NOT_FOUND, "회원을 찾을 수 없습니다."));

        } catch (IllegalArgumentException | MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));

        }
    }

    // 비밀번호 변경
    @PutMapping("/member/change-pwd")
    public ResponseEntity<ApiResponse<Map<String, String>>> changePassword(
            @RequestBody @Valid MemberRequest.ChangePwdDTO changePwdDTO,
            BindingResult bindingResult) {

        if (!memberService.checkOriginalPwd(changePwdDTO)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "기존 비밀번호와 같지 않습니다."));
        }

        changePasswordValidator.validate(changePwdDTO, bindingResult);

        if (bindingResult.hasErrors()) {

            Map<String, String> validatorResult = validateService.validateHandling(bindingResult);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "값이 validate 실패했습니다.", validatorResult));
        }

        try {
            if (memberService.changePwd(changePwdDTO)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.onSuccess(SuccessStatus._OK));

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, "비밀번호 업데이트에 실패했습니다."));
            }
        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/member")
    public ResponseEntity<ApiResponse<String>> deleteMember(
            @RequestParam(name="user_id") String userId,
            HttpServletRequest request, HttpServletResponse response
    ) {

        try {
            if (memberService.deleteMember(request, response, userId)) {

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
