package com.suni.api.jpprivateuserapi.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse extends Throwable {

    private final String code;

    private final String message;

    public ErrorResponse(ErrorCode errorCode) {

        this.code = errorCode.getCode();
        this.message = errorCode.getDescription();
    }
}
