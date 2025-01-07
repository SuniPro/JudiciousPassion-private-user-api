package com.suni.api.jpprivateuserapi.dto;

import com.suni.api.jpprivateuserapi.entity.user.RoleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDTO {

    private Integer id;

    private String username;

    private String phoneNumber;

    private String email;

    private String profileImage;

    private String profileMessage;

    private String personalColor;

    private String password;

    private RoleType roleType;

    private LocalDateTime insertDate;

    private String insertId;

    private LocalDateTime updateDate;

    private String updateId;

    private LocalDateTime deleteDate;

    private String deleteId;
}
