package com.example.everguide.service.mail;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.internal.http2.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public String createCode() {

        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) { // 인증 코드 8자리
            int index = random.nextInt(3); // 0~2까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // 소문자
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 2 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    public String sendEmail(String toEmail) {

        String code = createCode();
        String title = "Everguide 임시 비밀번호입니다.";
        String text = "임시 비밀번호는 [" + code + "] 입니다.";

        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);

        try {
            javaMailSender.send(emailForm);

            return code;

        } catch (RuntimeException e) {
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}
