package com.suni.api.jpprivateuserapi.service.user.impl;

import com.suni.api.jpprivateuserapi.dto.CustomUserDto;
import com.suni.api.jpprivateuserapi.dto.SignInRequestDTO;
import com.suni.api.jpprivateuserapi.dto.UserDTO;
import com.suni.api.jpprivateuserapi.entity.user.User;
import com.suni.api.jpprivateuserapi.repository.UserRepository;
import com.suni.api.jpprivateuserapi.response.UserStatus;
import com.suni.api.jpprivateuserapi.service.user.UserService;
import com.suni.api.jpprivateuserapi.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
    }

    public UserStatus createUser(UserDTO userDTO) {
        if (userCheck(userDTO.getEmail()).isPresent()) {
            return UserStatus.UC022;
        }

        User user = User.builder()
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .roleType(userDTO.getRoleType())
                .insertId(userDTO.getEmail())
                .build();

        userRepository.save(user);

        return UserStatus.UL100;
    }

    public String signIn(SignInRequestDTO signInRequestDTO) {
        Optional<User> optionalUser = userCheck(signInRequestDTO.getEmail());

        if (optionalUser.isEmpty()) {
            return UserStatus.UL012.getCode();
        }

        User user = optionalUser.get();


        if (!bCryptPasswordEncoder.matches(signInRequestDTO.getPassword(), user.getPassword())){
        System.out.println("user = " + bCryptPasswordEncoder.matches(signInRequestDTO.getPassword(), user.getPassword()));
            return UserStatus.UL012.getCode();
        }

        CustomUserDto info = modelMapper.map(user, CustomUserDto.class);

        return jwtUtil.createAccessToken(info);
    }


    public UserStatus passwordChange(UserDTO userDTO) {

        if (userCheck(userDTO.getEmail()).isEmpty()) {
            return UserStatus.UP022;
        }

        User user = User.builder()
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .build();

        userRepository.save(user);

        return UserStatus.UL100;
    }

    public Optional<User> userCheck(String email) {

        return userRepository.findByEmail(email);
    }
}

