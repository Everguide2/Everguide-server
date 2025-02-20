package com.example.everguide.service.sms;

import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.domain.Member;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.sms.SmsRequest;
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

    private final MemberRepository memberRepository;
    private final RedisUtils redisUtils;

    public CoolSmsService(MemberRepository memberRepository, RedisUtils redisUtils) {
        this.memberRepository = memberRepository;
        this.redisUtils = redisUtils;
    }

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendSMS(SmsRequest.SmsSendDTO smsSendDTO) throws NurigoMessageNotReceivedException, NurigoEmptyResponseException, NurigoUnknownException {

        String toPhoneNumber = smsSendDTO.getPhoneNumber();

        Member member = memberRepository.findByPhoneNumber(toPhoneNumber).orElse(null);

        if (member != null) {
            throw new MemberBadRequestException("존재하는 회원 정보가 있습니다.");
        }

        String authCode = generateAuthCode();

        Message message = new Message();
        message.setFrom(fromPhoneNumber);
        message.setTo(toPhoneNumber);
        message.setText("본인확인 인증번호는 " + authCode + "입니다.");

        messageService.send(message);

        redisUtils.setSmsAuthCode(toPhoneNumber, authCode, 60000*5L);
    }

    private String generateAuthCode() {

        Random random = new Random();
        int ranNum = 0;
        String ranStr = "";
        int letterNum = 7;
        StringBuilder resultStr = new StringBuilder();

        for (int i = 0; i < letterNum; i++) {

            ranNum = random.nextInt(9);
            ranStr = Integer.toString(ranNum);
            resultStr.append(ranStr);
        }

        return resultStr.toString();
    }

    public void verifyCode(SmsRequest.SmsVerifyDTO smsVerifyDTO) {

        String toPhoneNumber = smsVerifyDTO.getPhoneNumber();
        String savedCode = redisUtils.getSmsAuthCode(toPhoneNumber);
        String verifyCode = smsVerifyDTO.getVerifyCode();

        if (savedCode != null) {
            if (savedCode.equals(verifyCode)) {
                redisUtils.deleteSmsAuthCode(toPhoneNumber);
                redisUtils.setSmsAuthCode(toPhoneNumber, savedCode, 60000*60L);
                redisUtils.setSmsAuthCodeVerify(toPhoneNumber, verifyCode, 60000*60L);

            } else {
                throw new MemberBadRequestException("verify code invalidate");
            }
        } else {
            throw new MemberBadRequestException("verify code expired");
        }
    }
}
