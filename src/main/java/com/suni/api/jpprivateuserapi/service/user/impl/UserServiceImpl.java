package com.suni.api.jpprivateuserapi.service.user.impl;

import com.suni.api.jpprivateuserapi.dto.CustomUserDto;
import com.suni.api.jpprivateuserapi.dto.SignInRequestDTO;
import com.suni.api.jpprivateuserapi.dto.UserDTO;
import com.suni.api.jpprivateuserapi.entity.user.RoleType;
import com.suni.api.jpprivateuserapi.entity.user.User;
import com.suni.api.jpprivateuserapi.repository.UserRepository;
import com.suni.api.jpprivateuserapi.response.ErrorResponse;
import com.suni.api.jpprivateuserapi.response.UserStatus;
import com.suni.api.jpprivateuserapi.service.aws.S3Service;
import com.suni.api.jpprivateuserapi.service.user.UserService;
import com.suni.api.jpprivateuserapi.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper, S3Service s3Service) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
        this.s3Service = s3Service;
    }

    public UserStatus createUser(UserDTO userDTO) {
        if (userCheck(userDTO.getEmail(), userDTO.getUsername())) {
            return UserStatus.UC022;
        }

        User user = User.builder()
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .phoneNumber(userDTO.getPhoneNumber())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .roleType(userDTO.getRoleType())
                .insertId(userDTO.getEmail())
                .build();

        userRepository.save(user);

        return UserStatus.UL100;
    }

    public String signIn(SignInRequestDTO signInRequestDTO) {

        if (userCheckThroughEmail(signInRequestDTO.getEmail())) {
            return UserStatus.UL012.getCode();
        }

        Optional<User> byEmail = userRepository.findByEmail(signInRequestDTO.getEmail());
        User user = byEmail.get();

        if (!bCryptPasswordEncoder.matches(signInRequestDTO.getPassword(), user.getPassword())) {
            System.out.println("user = " + bCryptPasswordEncoder.matches(signInRequestDTO.getPassword(), user.getPassword()));
            return UserStatus.UL012.getCode();
        }

        CustomUserDto info = modelMapper.map(user, CustomUserDto.class);

        return jwtUtil.createAccessToken(info);
    }

    public UserStatus passwordChange(UserDTO userDTO) {

        if (userCheck(userDTO.getEmail(), userDTO.getUsername())) {
            return UserStatus.UP022;
        }

        User user = User.builder()
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .build();

        userRepository.save(user);

        return UserStatus.UL100;
    }

    public UserDTO profileImageChange(String email, MultipartFile multipartFile) {
        UserDTO userDTO = new UserDTO();
        String uploadPath;
        try {
            uploadPath = s3Service.upload(multipartFile);
            User user = User.builder().profileImage(uploadPath).build();
            User save = userRepository.save(user);

            modelMapper.map(save, userDTO);
        } catch (ErrorResponse e) {
            throw new RuntimeException(e);
        }
        return userDTO;
    }

    public UserDTO checkMe(String email) {
        UserDTO userDTO = new UserDTO();
        Optional<User> byEmail = userRepository.findByEmail(email);
        byEmail.ifPresent(user -> {
            modelMapper.map(user, userDTO);
        });
        return userDTO;
    }

    @Override
    public UserDTO profileMessageChange(UserDTO userDTO) {

        User user = userRepository.findById(userDTO.getId())
                .map(existingUser -> {
                    // 기존 User 업데이트: builder로 수정된 객체 생성
                    return existingUser.toBuilder()
                            .roleType(userDTO.getRoleType() != null ? userDTO.getRoleType() : existingUser.getRoleType())
                            .profileMessage(userDTO.getProfileMessage() != null ? userDTO.getProfileMessage() : existingUser.getProfileMessage())
                            .username(userDTO.getUsername() != null ? userDTO.getUsername() : existingUser.getUsername())
                            .build();
                })
                .orElseGet(() -> User.builder()
                        .roleType(userDTO.getRoleType())
                        .personalColor(userDTO.getProfileMessage())
                        .username(userDTO.getUsername())
                        .build());

        // 저장
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO profilePersonalColorChange(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .map(existingUser -> {
                    // 기존 User 업데이트: builder로 수정된 객체 생성
                    return existingUser.toBuilder()
                            .roleType(userDTO.getRoleType() != null ? userDTO.getRoleType() : existingUser.getRoleType())
                            .personalColor(userDTO.getPersonalColor() != null ? userDTO.getPersonalColor() : existingUser.getPersonalColor())
                            .username(userDTO.getUsername() != null ? userDTO.getUsername() : existingUser.getUsername())
                            .build();
                })
                .orElseGet(() -> User.builder()
                        .roleType(userDTO.getRoleType())
                        .personalColor(userDTO.getPersonalColor())
                        .username(userDTO.getUsername())
                        .build());

        // 저장
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    public boolean userCheckThroughEmail(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        return byEmail.isEmpty();
    }

    public boolean userCheck(String email, String username) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        Optional<User> byUsername = userRepository.findByUsername(username);

        return byEmail.isPresent() && byUsername.isPresent();
    }
}

