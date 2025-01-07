package com.suni.api.jpprivateuserapi.dto;

import com.suni.api.jpprivateuserapi.entity.user.RoleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomUserDto extends UserDTO {

    private String username;

    private String email;

    private String password;

    private RoleType roleType;

    private String profileImage;

    private String profileMessage;

    private String personalColor;

    private LocalDateTime insertDate;

    private String insertId;

    private LocalDateTime updateDate;

    private String updateId;

    private LocalDateTime deleteDate;

    private String deleteId;
}
