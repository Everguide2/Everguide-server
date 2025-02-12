package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.service.sms.CoolSmsService;
import com.example.everguide.web.dto.SmsRequest;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final CoolSmsService coolSmsService;
    private final RedisUtils redisUtils;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendSms(@RequestBody SmsRequest.SmsSendDTO smsSendDTO) {

        try {
            String toPhoneNumber = smsSendDTO.getPhoneNumber();
            String certificationCode = coolSmsService.sendSMS(toPhoneNumber);

            redisUtils.setSmsCertificationCode(toPhoneNumber, certificationCode);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK));

        } catch (NurigoMessageNotReceivedException e) {
            List<FailedMessage> failedMessageList = e.getFailedMessageList();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, failedMessageList.toString()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<String>> verifyCode(@RequestBody SmsRequest.SmsVerifyDTO smsVerifyDTO) {

        String toPhoneNumber = smsVerifyDTO.getPhoneNumber();
        String savedCode = redisUtils.getSmsCertificationCode(toPhoneNumber);
        String verifyCode = smsVerifyDTO.getVerifyCode();

        if (savedCode != null) {
            if (savedCode.equals(verifyCode)) {
                redisUtils.deleteSmsCertificationCode(toPhoneNumber);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.onSuccess(SuccessStatus._OK));

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "verify code invalidate"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, "verify code expired"));
        }
    }
}
