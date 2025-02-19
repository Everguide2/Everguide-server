package com.example.everguide.api.code.status;

import com.example.everguide.api.code.BaseCode;
import com.example.everguide.api.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    //공통 응답
    _OK(HttpStatus.OK, "COMMON200", "OK"),
    _CREATED(HttpStatus.CREATED, "COMMON201", "생성 완료"),
    _ACCEPTED(HttpStatus.ACCEPTED, "COMMON202", "요청 수락됨"),
    _NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON204", "콘텐츠 없음"),

    // 복지정책 관련 응답
    WELFARE_SEARCH_SUCCESS(HttpStatus.OK, "WELFARE2000", "복지정책 목록 조회가 완료되었습니다."),
    WELFARE_DETAIL_SUCCESS(HttpStatus.OK, "WELFARE2001", "복지정책 상세 조회가 완료되었습니다."),
    WELFARE_SYNC_SUCCESS(HttpStatus.OK, "WELFARE2002", "복지정책 데이터 동기화가 완료되었습니다."),
    WELFARE_BOOKMARK_SUCCESS(HttpStatus.OK, "WELFARE2003", "복지정책 북마크 처리가 완료되었습니다."),
    WELFARE_BOOKMARK_CANCEL_SUCCESS(HttpStatus.OK, "WELFARE2004", "복지정책 북마크 취소가 완료되었습니다."),

    // 정책 관련 
    _POLICY_SYNC_SUCCESS(HttpStatus.OK, "POLICY_2000", "정책 데이터 동기화가 성공적으로 완료되었습니다."),
    POLICY_SYNC_SUCCESS(HttpStatus.OK, "POLICY2001", "정책 데이터 동기화가 완료되었습니다.");

    // 교육 관련
    
    // 일자리 관련

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
