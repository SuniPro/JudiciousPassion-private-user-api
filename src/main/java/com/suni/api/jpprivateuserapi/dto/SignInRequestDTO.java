package com.suni.api.jpprivateuserapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequestDTO {

    private String email;

    private String password;
}
