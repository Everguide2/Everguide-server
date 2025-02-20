package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.service.sms.CoolSmsService;
import com.example.everguide.web.dto.sms.SmsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.FailedMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Sms", description = "전화번호 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final CoolSmsService coolSmsService;

    @Operation(summary = "전화번호 인증코드 전송", description = "전화번호 인증코드를 전송합니다.")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendSms(@RequestBody SmsRequest.SmsSendDTO smsSendDTO) {

        try {

            coolSmsService.sendSMS(smsSendDTO);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));

        } catch (NurigoMessageNotReceivedException e) {
            List<FailedMessage> failedMessageList = e.getFailedMessageList();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, failedMessageList.toString()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    @Operation(summary = "전화번호 인증코드 검증", description = "전화번호 인증코드를 검증합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<String>> verifyCode(@RequestBody SmsRequest.SmsVerifyDTO smsVerifyDTO) {

        try {

            coolSmsService.verifyCode(smsVerifyDTO);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK));

        } catch (MemberBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));
        }
    }
}
