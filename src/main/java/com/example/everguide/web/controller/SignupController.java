package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.service.member.MemberCommandService;
import com.example.everguide.service.member.MemberQueryService;
import com.example.everguide.validation.AdditionalInfoValidator;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SignupController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final RegisterValidator registerValidator;
    private final AdditionalInfoValidator additionalInfoValidator;

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
}
