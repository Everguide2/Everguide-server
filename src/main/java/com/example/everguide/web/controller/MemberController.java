package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.service.member.MemberCommandService;
import com.example.everguide.service.member.MemberQueryService;
import com.example.everguide.validation.AdditionalInfoValidator;
import com.example.everguide.validation.ChangePasswordValidator;
import com.example.everguide.validation.RegisterValidator;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final RegisterValidator registerValidator;
    private final AdditionalInfoValidator additionalInfoValidator;
    private final ChangePasswordValidator changePasswordValidator;

    // 테스트용
    @GetMapping("/member")
    public String memberP() {

        return "Member Test Controller";
    }

    @GetMapping("/signup/test")
    public String signupTest() {

        return "Signup Test Controller";
    }

    // OAuth2 기본 로그인 창 띄우지 않기 위함
    @GetMapping("/noauth")
    public ResponseEntity<ApiResponse<Map<String, String>>> noAuth() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED, "unauthorized"));
    }

    // 소셜 로그인 Refresh 토큰 쿠키 발급 후 Access 토큰 헤더 발급 위함
    @PostMapping("/cookie-to-header")
    public ResponseEntity<ApiResponse<String>> cookieToHeader(HttpServletRequest request, HttpServletResponse response) {

        try {
            Boolean full = memberCommandService.cookieToHeader(request, response);

            if (full) {
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

    // 일반 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberResponse.SignupNotValidateDTO>> localSignup(
            @RequestBody @Valid MemberRequest.SignupDTO signupDTO, BindingResult bindingResult
    ) {

        registerValidator.validate(signupDTO, bindingResult);

        if (bindingResult.hasErrors()) {

            Map<String, String> validatorResult = memberQueryService.validateHandling(bindingResult);

            MemberResponse.SignupNotValidateDTO signupNotValidateDTO = MemberResponse.SignupNotValidateDTO.builder()
                    .name(signupDTO.getName())
                    .birth(signupDTO.getBirth())
                    .phoneNumber(signupDTO.getPhoneNumber())
                    .email(signupDTO.getEmail())
                    .password(signupDTO.getPassword())
                    .validatorResult(validatorResult)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "값이 validate 실패했습니다.", signupNotValidateDTO));
        }

        try {
            if (memberCommandService.localSignUp(signupDTO)) {

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.onSuccess(SuccessStatus._OK));

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, "회원가입에 실패했습니다."));
            }

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }

    // email 중복 확인
    @PostMapping("/signup/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestBody MemberRequest.SignupEmailDTO signupEmailDTO) {

        String email = signupEmailDTO.getEmail();
        String userId = "LOCAL_" + email;

        if (memberQueryService.checkEmailExist(userId)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "사용 가능하지 않은 이메일입니다."));
        }
    }

    // 소셜 회원가입 추가 정보 입력창
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

    // 소셜 회원가입 추가 정보 입력
    @PostMapping("/signup/additional-info")
    public ResponseEntity<ApiResponse<MemberResponse.AdditionalNotValidateDTO>> registerSignupAdditionalInfo(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody @Valid MemberRequest.SignupAdditionalDTO signupAdditionalDTO,
            BindingResult bindingResult
    ) {

        MemberResponse.AdditionalNotValidateDTO checkInfoEqual = memberQueryService.checkInfoEqual(request, response, signupAdditionalDTO);

        if (checkInfoEqual != null) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._UNFULL_ADDITIONAL_INFO, "저장된 값과 입력한 값이 같지 않습니다.", checkInfoEqual));
        }

        additionalInfoValidator.validate(signupAdditionalDTO, bindingResult);

        if (bindingResult.hasErrors()) {

            Map<String, String> validatorResult = memberQueryService.validateHandling(bindingResult);

            MemberResponse.AdditionalNotValidateDTO additionalNotValidateDTO = MemberResponse.AdditionalNotValidateDTO.builder()
                    .name(signupAdditionalDTO.getName())
                    .birth(LocalDate.parse(signupAdditionalDTO.getBirth(), DateTimeFormatter.BASIC_ISO_DATE))
                    .phoneNumber(signupAdditionalDTO.getPhoneNumber())
                    .email(signupAdditionalDTO.getEmail())
                    .validatorResult(validatorResult)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "값이 validate 실패했습니다.", additionalNotValidateDTO));
        }

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

    // 비밀번호 변경
    @PutMapping("/member/change-pwd")
    public ResponseEntity<ApiResponse<Map<String, String>>> changePassword(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody @Valid MemberRequest.ChangePwdDTO changePwdDTO,
            BindingResult bindingResult) {

        if (!memberQueryService.checkOriginalPwd(request, response, changePwdDTO)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "기존 비밀번호와 같지 않습니다."));
        }

        changePasswordValidator.validate(changePwdDTO, bindingResult);

        if (bindingResult.hasErrors()) {

            Map<String, String> validatorResult = memberQueryService.validateHandling(bindingResult);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "값이 validate 실패했습니다.", validatorResult));
        }

        try {
            if (memberCommandService.changePwd(request, response, changePwdDTO)) {
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

    // 이메일 찾기
    @PostMapping("/find-email")
    public ResponseEntity<ApiResponse<MemberResponse.FindEmailDTO>> findEmail(
            @RequestBody MemberRequest.FindEmailDTO findEmailDTO) {

        try {
            MemberResponse.FindEmailDTO findEmailResponseDTO = memberCommandService.findEmail(findEmailDTO);

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
            if (memberCommandService.findPwd(findPwdDTO)) {
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

    // 회원 탈퇴
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
