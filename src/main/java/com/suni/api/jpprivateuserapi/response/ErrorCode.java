package com.suni.api.jpprivateuserapi.response;

import lombok.Getter;

@Getter
public enum ErrorCode {

    EMPTY_FILE_EXCEPTION(400, "EMPTY_FILE_EXCEPTION", "전송하려는 파일이 없습니다."),

    IO_EXCEPTION_ON_IMAGE_UPLOAD(500, "IO_EXCEPTION_ON_IMAGE_UPLOAD", "파일 업로드에 실패했습니다."),

    NO_FILE_EXCEPTION(400, "NO_FILE_EXTENTION", "파일이 없습니다."),

    INVALID_FILE_EXCEPTION(400, "INVALID_FILE_EXTENTION", "정상적인 파일이 아닙니다."),

    PUT_OBJECT_EXCEPTION(500, "PUT_OBJECT_EXCEPTION", "객체 입력에 실패하였습니다."),

    NON_LOGIN(400, "NON_TOKEN.", "토큰이 없습니다."),
    EXPIRED_TOKEN(400, "EXPIRED_TOKEN", "이미 만료된 토큰입니다."),
    INVALID_TOKEN(400, "INVALID_TOKEN", "토큰의 시그니처가 일치하지 않습니다."),

    IO_EXCEPTION_ON_IMAGE_DELETE(500, "IO_EXCEPTION_ON_IMAGE_DELETE", "삭제를 실패했습니다."),

    CANNOT_BOARD_CREATE(400, "CANNOT_BOARD_CREATE", "제목 혹은 작성자가 없습니다.");

    private final int status;
    private final String code;
    private final String description;

    ErrorCode(int status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }

}
