package com.suni.api.jpprivateuserapi.response;

import lombok.Getter;

/**
 * 정상, 비 정상의 통신상태를 분명히 하기 위해서 작성된 클래스입니다.
 * code는 세개의 숫자열을 사용하며,
 * 첫번째는 상태를 (0 비정상, 1 정상)
 * 두번째는 문제가 발생한 구역 (0 controller, 1 service, 2 dao),
 * 세번째는 임의로 지정하는 문제의 중요도입니다.
 * '0' 은 특정할 수 없는 문제, '1' 은 서버의 실패, '2' 는 사용자의 실수로 지정합니다.
 * u : undefined, l : login, p : password 등 두번째 문자는 쉽게 판독할 수 있는 약어를 사용합니다.
 */
@Getter
public enum UserStatus {

    UU000("000", "알 수 없는 문제로 해당 작업을 수행 할 수 없습니다."),

    UE002("002", "해당 이메일은 'STYLE LEADER' 의 이메일이 아닙니다."),

    UC022("022", "이미 존재하는 아이디입니다."),

    UL012("012", "아이디 혹은 비밀번호를 확인해주세요."),

    UP022("022", "이전 패스워드와 동일합니다."),

    UL100("100", "운영자님 환영합니다."),

    UL101("101", "환영합니다."),

    UO101("101", "로그아웃 되었습니다.");

    private String code;
    private String message;

    UserStatus(String code, String message) {
    }

}
