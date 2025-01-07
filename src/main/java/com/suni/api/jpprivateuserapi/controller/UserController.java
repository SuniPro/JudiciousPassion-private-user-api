package com.suni.api.jpprivateuserapi.controller;

import com.suni.api.jpprivateuserapi.dto.SignInRequestDTO;
import com.suni.api.jpprivateuserapi.dto.UserDTO;
import com.suni.api.jpprivateuserapi.response.UserStatus;
import com.suni.api.jpprivateuserapi.service.user.UserService;
import com.suni.api.jpprivateuserapi.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, JwtUtil jwtUtil) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/check")
    public ResponseEntity<UserDTO> realMe(HttpServletRequest request) {
        String token = getCookieValue(request);

        if (token == null) {
            return ResponseEntity.ok().body(null);
        }

        boolean validatedToken = jwtUtil.validateToken(token);
        if (!validatedToken) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok().body(userService.checkMe(jwtUtil.getUserEmail(token)));
    }

    @PostMapping("/create")
    public ResponseEntity<UserStatus> signUp(@RequestBody UserDTO userDTO) {
        UserStatus status = userService.createUser(userDTO);

        SignInRequestDTO signInRequestDTO = modelMapper.map(userDTO, SignInRequestDTO.class);

        if (UserStatus.UC022 == status) {
            return ResponseEntity.badRequest().body(UserStatus.UC022);
        }
        String token = userService.signIn(signInRequestDTO);
        ResponseCookie responseCookie = ResponseCookie.from("access-token", token).path("/").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(status);
    }

    @PostMapping("/login")
    public ResponseEntity<UserStatus> signIn(@RequestBody SignInRequestDTO signInRequestDTO) {

        String result = userService.signIn(signInRequestDTO);
        if (result == null) {
            return ResponseEntity.badRequest().body(UserStatus.UL012);
        }

        ResponseCookie responseCookie = ResponseCookie.from("access-token", result).path("/").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(UserStatus.UL101);
    }

    @GetMapping("/logout")
    public ResponseEntity<UserStatus> logout() {
        ResponseCookie responseCookie = ResponseCookie.from("access-token", "").path("/").maxAge(0).build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(UserStatus.UL100);
    }

    @PostMapping("/profile/image/upload")
    public ResponseEntity<UserDTO> uploadProfileFile(@RequestParam(value = "file", required = false) MultipartFile multipartFile, @RequestParam(value = "email", required = false) String email, HttpServletRequest request){

        String token = getCookieValue(request);

        if (token == null) {
            return ResponseEntity.ok().body(null);
        }

        if (multipartFile.isEmpty()) {
            log.info("File is empty");
        }

        boolean validatedToken = jwtUtil.validateToken(token);
        if (!validatedToken) {
            return ResponseEntity.badRequest().body(null);
        }

        UserDTO result = userService.profileImageChange(email, multipartFile);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/profile/message/upload")
    public ResponseEntity<UserDTO> uploadProfileMessage(@RequestBody UserDTO userDTO, HttpServletRequest request){

        String token = getCookieValue(request);

        if (token == null) {
            return ResponseEntity.ok().body(null);
        }

        boolean validatedToken = jwtUtil.validateToken(token);
        if (!validatedToken) {
            return ResponseEntity.badRequest().body(null);
        }

        UserDTO result = userService.profileMessageChange(userDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/profile/color/upload")
    public ResponseEntity<UserDTO> uploadPersonalColor(@RequestBody UserDTO userDTO ,HttpServletRequest request){

        String token = getCookieValue(request);

        if (token == null) {
            return ResponseEntity.ok().body(null);
        }

        boolean validatedToken = jwtUtil.validateToken(token);
        if (!validatedToken) {
            return ResponseEntity.badRequest().body(null);
        }

        UserDTO result = userService.profilePersonalColorChange(userDTO);
        return ResponseEntity.ok(result);
    }

    private String getCookieValue(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("access-token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
