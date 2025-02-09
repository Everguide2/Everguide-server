package com.example.everguide.api.exception.handler;

import com.example.everguide.api.code.BaseErrorCode;
import com.example.everguide.api.exception.GeneralException;

public class MemberExceptionHandler extends GeneralException {

    public MemberExceptionHandler(BaseErrorCode code) {
        super(code);
    }
}
