package com.example.everguide.service.sms;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CoolSmsService {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String fromPhoneNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public String sendSMS(String toPhoneNumber) throws NurigoMessageNotReceivedException, NurigoEmptyResponseException, NurigoUnknownException {

        String authCode = generateAuthCode();

        Message message = new Message();
        message.setFrom(fromPhoneNumber);
        message.setTo(toPhoneNumber);
        message.setText("본인확인 인증번호는 " + authCode + "입니다.");


        messageService.send(message);
        return authCode;
    }

    private String generateAuthCode() {

        Random random = new Random();
        int ranNum = 0;
        String ranStr = "";
        int letterNum = 6;
        StringBuilder resultStr = new StringBuilder();

        for (int i = 0; i < letterNum; i++) {

            ranNum = random.nextInt(9);
            ranStr = Integer.toString(ranNum);
            resultStr.append(ranStr);
        }

        return resultStr.toString();
    }
}
