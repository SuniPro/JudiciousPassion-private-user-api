package com.suni.api.jpprivateuserapi.service.user;

import com.suni.api.jpprivateuserapi.dto.SignInRequestDTO;
import com.suni.api.jpprivateuserapi.dto.UserDTO;
import com.suni.api.jpprivateuserapi.entity.user.RoleType;
import com.suni.api.jpprivateuserapi.response.UserStatus;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserStatus createUser(UserDTO userDTO);

    String signIn(SignInRequestDTO signInRequestDTO);

    UserStatus passwordChange(UserDTO userDTO);

    UserDTO profileImageChange(String email, MultipartFile multipartFile);

    UserDTO profileMessageChange(UserDTO userDTO);

    UserDTO profilePersonalColorChange(UserDTO userDTO);

    UserDTO checkMe(String email);
}
