package com.example.everguide.service.auth;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.ProviderType;
import com.example.everguide.domain.enums.Role;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.auth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Value("${social-login.password}")
    private String socialPwd;

    private final MemberRepository memberRepository;
    private final RedisUtils redisUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 네이버, 카카오 받은 userRequest 데이터에 대한 후처리 되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String userId = oAuth2Response.getProviderType().name()+"_"+oAuth2Response.getProviderId();
        String encPassword = bCryptPasswordEncoder.encode(socialPwd);

        String socialAccessToken = userRequest.getAccessToken().getTokenValue();
        String socialRefreshToken = (String) userRequest.getAdditionalParameters().get(OAuth2ParameterNames.REFRESH_TOKEN);
        redisUtils.setSocialAccessToken(userId, socialAccessToken, 1L);
        redisUtils.setSocialRefreshToken(userId, socialRefreshToken, 1L);

        Optional<Member> existMemberOptional = memberRepository.findByUserId(userId);

        if (existMemberOptional.isEmpty()) {

            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setUserId(userId);
            memberDTO.setName(oAuth2Response.getName());

            memberDTO.setRole(Role.ROLE_PRE_MEMBER.name());
            if (oAuth2Response.getName() != null && !oAuth2Response.getName().isBlank()) {
                if (oAuth2Response.getBirthyear() != null && !oAuth2Response.getBirthyear().isBlank()) {
                    if (oAuth2Response.getBirthday() != null && !oAuth2Response.getBirthday().isBlank()) {
                        if (oAuth2Response.getEmail() != null && !oAuth2Response.getEmail().isBlank()) {
                            if (oAuth2Response.getPhoneNumber() != null && !oAuth2Response.getPhoneNumber().isBlank()) {
                                memberDTO.setRole(Role.ROLE_MEMBER.name());
                            }
                        }
                    }
                }
            }

            if (oAuth2Response.getProviderType().equals(ProviderType.NAVER)) {
                Member member = naverSaveMember(oAuth2Response, userId, encPassword);
                memberDTO.setSocial("naver");

            } else if (oAuth2Response.getProviderType().equals(ProviderType.KAKAO)) {
                Member member = kakaoSaveMember(oAuth2Response, userId, encPassword);
                memberDTO.setSocial("kakao");

            } else {
                throw new MemberBadRequestException(ErrorStatus._INVALID_PROVIDER_TYPE.getMessage());
            }

            return new CustomOAuth2User(memberDTO);

        } else {

            Member existMember = existMemberOptional.get();

            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setUserId(existMember.getUserId());
            memberDTO.setName(existMember.getName());
            memberDTO.setRole(existMember.getRole().name());

            if (existMember.getProviderType().equals(ProviderType.NAVER)) {
                Member member = naverUpdateMember(existMember, oAuth2Response, userId);
                memberDTO.setSocial("naver");

            } else if (existMember.getProviderType().equals(ProviderType.KAKAO)) {
                Member member = kakaoUpdateMember(existMember, oAuth2Response, userId);
                memberDTO.setSocial("kakao");

            } else {
                throw new MemberBadRequestException(ErrorStatus._INVALID_PROVIDER_TYPE.getMessage());
            }

            return new CustomOAuth2User(memberDTO);
        }
    }

    private Member kakaoSaveMember(OAuth2Response oAuth2Response, String userId, String encPassword) {

        String email = oAuth2Response.getEmail();
        String name = oAuth2Response.getName();
        String birthday = oAuth2Response.getBirthday();
        String birthyear = oAuth2Response.getBirthyear();
        LocalDate birth;
        if (birthday != null && birthyear != null) {
            birth = LocalDate.parse(birthyear+birthday, DateTimeFormatter.BASIC_ISO_DATE);
        } else {
            birth = null;
        }
        String phoneNumber = oAuth2Response.getPhoneNumber();
        if (phoneNumber != null) {
            phoneNumber = "0" + phoneNumber.substring(4,6) + phoneNumber.substring(7, 11) + phoneNumber.substring(12);
        }
        Role role = Role.ROLE_PRE_MEMBER;
        if (oAuth2Response.getName() != null && !oAuth2Response.getName().isBlank()) {
            if (oAuth2Response.getBirthyear() != null && !oAuth2Response.getBirthyear().isBlank()) {
                if (oAuth2Response.getBirthday() != null && !oAuth2Response.getBirthday().isBlank()) {
                    if (oAuth2Response.getEmail() != null && !oAuth2Response.getEmail().isBlank()) {
                        if (oAuth2Response.getPhoneNumber() != null && !oAuth2Response.getPhoneNumber().isBlank()) {
                            role = Role.ROLE_MEMBER;
                        }
                    }
                }
            }
        }
        ProviderType providerType = oAuth2Response.getProviderType();

        Member member = Member.builder()
                .email(email)
                .name(name)
                .birth(birth)
                .phoneNumber(phoneNumber)
                .password(encPassword)
                .role(role)
                .providerType(providerType)
                .userId(userId)
                .build();

        return memberRepository.save(member);
    }

    private Member naverSaveMember(OAuth2Response oAuth2Response, String userId, String encPassword) {

        String email = oAuth2Response.getEmail();
        String name = oAuth2Response.getName();
        String birthday = oAuth2Response.getBirthday();
        String birthyear = oAuth2Response.getBirthyear();
        LocalDate birth;
        if (birthday != null && birthyear != null) {
            birth = LocalDate.parse(birthyear+birthday.substring(0,2)+birthday.substring(3), DateTimeFormatter.BASIC_ISO_DATE);
        } else {
            birth = null;
        }
        String phoneNumber = oAuth2Response.getPhoneNumber();
        phoneNumber = phoneNumber.substring(0, 3) + phoneNumber.substring(4,8) + phoneNumber.substring(9);
        Role role = Role.ROLE_PRE_MEMBER;
        if (oAuth2Response.getName() != null && !oAuth2Response.getName().isBlank()) {
            if (oAuth2Response.getBirthyear() != null && !oAuth2Response.getBirthyear().isBlank()) {
                if (oAuth2Response.getBirthday() != null && !oAuth2Response.getBirthday().isBlank()) {
                    if (oAuth2Response.getEmail() != null && !oAuth2Response.getEmail().isBlank()) {
                        if (oAuth2Response.getPhoneNumber() != null && !oAuth2Response.getPhoneNumber().isBlank()) {
                            role = Role.ROLE_MEMBER;
                        }
                    }
                }
            }
        }
        ProviderType providerType = oAuth2Response.getProviderType();

        Member member = Member.builder()
                .email(email)
                .name(name)
                .birth(birth)
                .phoneNumber(phoneNumber)
                .password(encPassword)
                .role(role)
                .providerType(providerType)
                .userId(userId)
                .build();

        return memberRepository.save(member);
    }

    private Member kakaoUpdateMember(Member existMember, OAuth2Response oAuth2Response, String userId) {

        String email = oAuth2Response.getEmail();
        String name = oAuth2Response.getName();
        String birthday = oAuth2Response.getBirthday();
        String birthyear = oAuth2Response.getBirthyear();
        LocalDate birth;
        if (birthday != null && birthyear != null) {
            birth = LocalDate.parse(birthyear+birthday, DateTimeFormatter.BASIC_ISO_DATE);
        } else {
            birth = null;
        }
        String phoneNumber = oAuth2Response.getPhoneNumber();
        if (phoneNumber != null) {
            phoneNumber = "0" + phoneNumber.substring(4,6) + phoneNumber.substring(7, 11) + phoneNumber.substring(12);
        }

        existMember.setEmail(email);
        existMember.setName(name);
        existMember.setBirth(birth);
        existMember.setPhoneNumber(phoneNumber);
        existMember.setUserId(userId);

        return memberRepository.save(existMember);
    }

    private Member naverUpdateMember(Member existMember, OAuth2Response oAuth2Response, String userId) {

        String email = oAuth2Response.getEmail();
        String name = oAuth2Response.getName();
        String birthday = oAuth2Response.getBirthday();
        String birthyear = oAuth2Response.getBirthyear();
        LocalDate birth;
        if (birthday != null && birthyear != null) {
            birth = LocalDate.parse(birthyear+birthday.substring(0,2)+birthday.substring(3), DateTimeFormatter.BASIC_ISO_DATE);
        } else {
            birth = null;
        }
        String phoneNumber = oAuth2Response.getPhoneNumber();
        phoneNumber = phoneNumber.substring(0, 3) + phoneNumber.substring(4,8) + phoneNumber.substring(9);

        existMember.setEmail(email);
        existMember.setName(name);
        existMember.setBirth(birth);
        existMember.setPhoneNumber(phoneNumber);
        existMember.setUserId(userId);

        return memberRepository.save(existMember);
    }
}
