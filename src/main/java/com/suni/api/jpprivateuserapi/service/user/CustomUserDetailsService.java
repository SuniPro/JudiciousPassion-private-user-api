package com.suni.api.jpprivateuserapi.service.user;

import com.suni.api.jpprivateuserapi.dto.CustomUserDto;
import com.suni.api.jpprivateuserapi.entity.user.User;
import com.suni.api.jpprivateuserapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

        CustomUserDto dto = mapper.map(user, CustomUserDto.class);

        return new CustomUserDetails(dto);
    }
}
