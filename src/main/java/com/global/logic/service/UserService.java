package com.global.logic.service;

import com.global.logic.dtos.SignUpRequestDTO;
import com.global.logic.dtos.UserResponseDTO;

import org.springframework.stereotype.Service;

import com.global.logic.dtos.LoginResponseDTO;

@Service
public interface UserService {
    UserResponseDTO registerUser(SignUpRequestDTO signUpRequest);

    LoginResponseDTO loginUser(String token);
}
