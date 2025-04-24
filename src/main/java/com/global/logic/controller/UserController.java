package com.global.logic.controller;

import com.global.logic.dtos.LoginResponseDTO;
import com.global.logic.dtos.SignUpRequestDTO;
import com.global.logic.dtos.UserResponseDTO;
import com.global.logic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequest) {
        UserResponseDTO response = userService.registerUser(signUpRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestHeader("Authorization") String authorizationHeader) {
        LoginResponseDTO response = userService.loginUser(authorizationHeader);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
