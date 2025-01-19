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
    _NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON204", "콘텐츠 없음")

    //멤버 관련 응답

    ;


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
