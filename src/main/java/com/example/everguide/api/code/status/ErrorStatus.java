package com.example.everguide.api.code.status;

import com.example.everguide.api.code.BaseErrorCode;
import com.example.everguide.api.code.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 공통 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 내부 오류 발생"),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON4001", "인증되지 않은 요청입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON4002", "접근이 거부되었습니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON4003", "요청한 리소스를 찾을 수 없습니다."),

    // 네이버 소셜 로그인 관련 에러
    _NAVER_SIGN_IN_INTEGRATION_FAILED(HttpStatus.UNAUTHORIZED, "NAVER4001", "네이버 로그인 연동에 실패하였습니다."),
    _NAVER_ACCESS_TOKEN_ISSUANCE_FAILED(HttpStatus.UNAUTHORIZED, "NAVER4002", "네이버 액세스 토큰 발급에 실패하였습니다."),

    // 로그인 관련 에러
    _INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "MEMBER5001", "해당 회원이 아닙니다."),
    _INVALID_PROVIDER_TYPE(HttpStatus.BAD_REQUEST, "MEMBER5002", "사용가능한 ProviderType이 아닙니다."),

    // 소셜 로그인 관련 에러,
    _JSON_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SOCIAL5001", "JSON 프로세싱 중 오류 발생"),
    _PROVIDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "SOCIAL4001", "providerType을 찾을 수 없습니다."),
    _USED_EMAIL(HttpStatus.BAD_REQUEST, "SOCIAL4002", "이미 사용된 이메일입니다."),
    _LOCAL_INVALID_TOKEN(HttpStatus.BAD_REQUEST, "SOCIAL4003", "로컬에 존재하지 않는 토큰입니다."),
    _SOCIAL_INVALID_TOKEN(HttpStatus.BAD_REQUEST, "SOCIAL4003", "소셜 로그인에 유효하지 않은 토큰입니다."),
    _TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "SOCIAL4004", "만료된 토큰입니다."),
    _TOKEN_NULL(HttpStatus.BAD_REQUEST, "SOCIAL4005", "토큰이 null입니다."),
    _NO_TOKEN(HttpStatus.BAD_REQUEST, "SOCIAL4006", "토큰이 헤더에 존재하지 않습니다."),
    _UNFULL_ADDITIONAL_INFO(HttpStatus.NOT_FOUND, "SOCIAL4007", "회원정보가 전부 채워지지 않았습니다."),


    // 카카오 소셜 로그인 관련 에러
    //멤버 관련 에러
    _MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "존재하지 않는 멤버입니다"),

    //일자리 관련 에러
    _JOB_NOT_FOUND(HttpStatus.BAD_REQUEST, "JOB4000" , "존재하지 않는 일자리입니다."),
    _THIS_WEEK_JOB_NOT_FOUND(HttpStatus.BAD_REQUEST, "JOB4001" , "이번주에 마감되는 일자리가 없습니다"),

    //북마크 관련 에러
    _JOB_BOOKMARK_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "BOOKMARK4001", "이미 북마크 처리 되었습니다."),
    _JOB_BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOOKMARK4002", "직업이 북마크 처리 되어 있지 않습니다."),
    _EDUCATION_BOOKMARK_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "BOOKMARK4003", "이미 북마크 처리 되었습니다."),
    _EDUCATION_BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOOKMARK4004", "교육이 북마크 처리 되어 있지 않습니다."),


    //교육 관련
    _EDUCATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "EDU4001", "존재하지 않는 교육입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build()
                ;
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
