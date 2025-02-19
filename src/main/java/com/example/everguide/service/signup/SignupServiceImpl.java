package com.example.everguide.service.signup;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.ProviderType;
import com.example.everguide.domain.enums.Role;
import com.example.everguide.jwt.JWTUtil;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.member.MemberResponse;
import com.example.everguide.web.dto.signup.SignupRequest;
import com.example.everguide.web.dto.signup.SignupResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignupServiceImpl implements SignupService {

    private final MemberRepository memberRepository;

    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtils redisUtils;

    @Override
    public Boolean checkEmailExist(String userId) {

        Member member = memberRepository.findByUserId(userId).orElse(null);

        return member == null;
    }

    @Override
    public SignupResponse.AdditionalNotValidateDTO checkInfoEqual(HttpServletRequest request, HttpServletResponse response,
                                                                  SignupRequest.SignupAdditionalDTO signupAdditionalDTO) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            throw new MemberBadRequestException(ErrorStatus._NO_TOKEN.getMessage());
        }

        String accessToken = authorization.split(" ")[1];

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            throw new MemberBadRequestException(ErrorStatus._TOKEN_EXPIRED.getMessage());
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            throw new MemberBadRequestException("Access Token이 아닙니다.");
        }

        String userId = jwtUtil.getUserId(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        Boolean infoEqual = true;
        String checkName;
        LocalDate checkBirth;
        String checkPhoneNumber;
        String checkEmail;

        if (member.getName() != null) {
            if (!member.getName().equals(signupAdditionalDTO.getName())) {
                infoEqual = false;
            }
            checkName = member.getName();
        } else {
            checkName = null;
        }

        if (member.getBirth() != null) {
            String birth = member.getBirth().format(DateTimeFormatter.BASIC_ISO_DATE);
            if (!birth.equals(signupAdditionalDTO.getBirth())) {
                infoEqual = false;
            }
            checkBirth = member.getBirth();
        } else {
            checkBirth = null;
        }

        if (member.getPhoneNumber() != null) {
            if (!member.getPhoneNumber().equals(signupAdditionalDTO.getPhoneNumber())) {
                infoEqual = false;
            }
            checkPhoneNumber = member.getPhoneNumber();
        } else {
            checkPhoneNumber = null;
        }

        if (member.getEmail() != null) {
            if (!member.getEmail().equals(signupAdditionalDTO.getEmail())) {
                infoEqual = false;
            }
            checkEmail = member.getEmail();
        } else {
            checkEmail = null;
        }

        if (infoEqual) {
            return null;

        } else {
            return SignupResponse.AdditionalNotValidateDTO.builder()
                    .name(checkName)
                    .birth(checkBirth)
                    .phoneNumber(checkPhoneNumber)
                    .email(checkEmail)
                    .validatorResult(null)
                    .build();
        }
    }

    @Override
    @Transactional
    public boolean localSignUp(SignupRequest.SignupDTO signupDTO) {

        String name = signupDTO.getName();
        String birth = signupDTO.getBirth();
        String phoneNumber = signupDTO.getPhoneNumber();
        String email = signupDTO.getEmail();
        String password = signupDTO.getPassword();
        String userId = "LOCAL_" + email;

        checkEmailDuplicate(userId);

        checkPhoneNumberDuplicate(phoneNumber);

        checkSmsVerify(phoneNumber);

        Member member = Member.builder()
                .name(name)
                .birth(LocalDate.parse(birth, DateTimeFormatter.BASIC_ISO_DATE))
                .phoneNumber(phoneNumber)
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .role(Role.ROLE_MEMBER)
                .providerType(ProviderType.LOCAL)
                .userId(userId)
                .build();

        memberRepository.save(member);

        return true;
    }

    private Boolean checkSmsVerify(String phoneNumber) {

        String savedCode = redisUtils.getSmsAuthCode(phoneNumber);
        String verifyCode = redisUtils.getSmsAuthCodeVerify(phoneNumber);

        if (savedCode == null || verifyCode == null) {
            throw new MemberBadRequestException("인증코드를 찾을 수 없습니다.");
        }

        if (!savedCode.equals(verifyCode)) {
            throw new MemberBadRequestException("인증코드가 일치하지 않습니다.");
        }

        return true;
    }

    private Boolean checkEmailDuplicate(String userId) {

        if (memberRepository.existsByUserId(userId)) {
            throw new MemberBadRequestException("이미 존재하는 이메일입니다.");

        } else {
            return true;
        }
    }

    private Boolean checkPhoneNumberDuplicate(String phoneNumber) {

        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new MemberBadRequestException("이미 존재하는 전화번호입니다.");

        } else {
            return true;
        }
    }

    @Override
    @Transactional
    public SignupResponse.SignupAdditionalDTO getSignupAdditionalInfo(HttpServletRequest request, HttpServletResponse response) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            throw new MemberBadRequestException(ErrorStatus._NO_TOKEN.getMessage());
        }

        String accessToken = authorization.split(" ")[1];

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            throw new MemberBadRequestException(ErrorStatus._TOKEN_EXPIRED.getMessage());
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            throw new MemberBadRequestException("Access Token이 아닙니다.");
        }

        String userId = jwtUtil.getUserId(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

        return SignupResponse.SignupAdditionalDTO.builder()
                .name(member.getName())
                .birth(member.getBirth())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .build();
    }

    @Override
    @Transactional
    public boolean registerSignupAdditionalInfo(
            HttpServletRequest request, HttpServletResponse response,
            SignupRequest.SignupAdditionalDTO signupAdditionalDTO) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            throw new MemberBadRequestException(ErrorStatus._NO_TOKEN.getMessage());
        }

        String accessToken = authorization.split(" ")[1];

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            throw new MemberBadRequestException(ErrorStatus._TOKEN_EXPIRED.getMessage());
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            throw new MemberBadRequestException("Access Token이 아닙니다.");
        }

        String userId = jwtUtil.getUserId(accessToken);
        String social = jwtUtil.getSocial(accessToken);

        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

        member.setName(signupAdditionalDTO.getName());
        LocalDate birth = LocalDate.parse(signupAdditionalDTO.getBirth(), DateTimeFormatter.BASIC_ISO_DATE);
        member.setBirth(birth);
        member.setPhoneNumber(signupAdditionalDTO.getPhoneNumber());
        member.setEmail(signupAdditionalDTO.getEmail());
        member.setRole(Role.ROLE_MEMBER);

        memberRepository.save(member);

        String access = jwtUtil.createJwt(userId, Role.ROLE_MEMBER.name(), social, "access", 60000*10L);
        String refresh = jwtUtil.createJwt(userId, Role.ROLE_MEMBER.name(), social, "refresh", 60000*60*24L);

        redisUtils.changeLocalRefreshToken(userId, refresh, 60000*60*24L);

        response.addHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        return true;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
