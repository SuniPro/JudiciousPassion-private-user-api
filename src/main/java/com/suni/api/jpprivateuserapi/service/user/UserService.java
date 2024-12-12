package com.suni.api.jpprivateuserapi.service.user;

import com.suni.api.jpprivateuserapi.dto.SignInRequestDTO;
import com.suni.api.jpprivateuserapi.dto.UserDTO;
import com.suni.api.jpprivateuserapi.entity.user.User;
import com.suni.api.jpprivateuserapi.response.UserStatus;

import java.util.Optional;

public interface UserService {

    UserStatus createUser(UserDTO userDTO);

    String signIn(SignInRequestDTO signInRequestDTO);

    UserStatus passwordChange(UserDTO userDTO);

    Optional<User> userCheck(String email);
}
