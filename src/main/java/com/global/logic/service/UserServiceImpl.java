package com.global.logic.service;

import com.global.logic.dtos.*;
import com.global.logic.model.Phone;
import com.global.logic.model.User;
import com.global.logic.data.UserRepository;
import com.global.logic.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    

    @Override
    @Transactional
    public UserResponseDTO registerUser(SignUpRequestDTO signUpRequest) {
        // 1. Check if the email already exists
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: " + signUpRequest.getEmail());
        }

        // 2. Create the User entity
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        // 3. Encrypt the password
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // 4. Map PhoneDTOs to Phone entities (Java 8+ Streams)
        if (signUpRequest.getPhones() != null) {
            user.setPhones(signUpRequest.getPhones().stream()
                .map(dto -> {
                    Phone phone = new Phone();
                    phone.setNumber(dto.getNumber());
                    phone.setCityCode(dto.getCityCode());
                    phone.setCountryCode(dto.getCountryCode());
                    phone.setUser(user);
                    return phone;
                })
                .collect(Collectors.toList()));
        }

        // 5. Generate the initial jwt token
        String token = tokenProvider.generateToken(user.getEmail());
        user.setToken(token);

        // 6. Save the user in the database
        User savedUser = userRepository.save(user);

        // 7. Create and return the response DTO
        List<PhoneDTO> phoneDTOs = Optional.ofNullable(savedUser.getPhones())
        .orElse(Collections.emptyList())
        .stream()
        .map(phone -> {
            PhoneDTO dto = new PhoneDTO();
            dto.setNumber(phone.getNumber());
            dto.setCityCode(phone.getCityCode());
            dto.setCountryCode(phone.getCountryCode());
            return dto;
        })
        .collect(Collectors.toList());

return new UserResponseDTO(
        savedUser.getId(),
        savedUser.getCreated(),
        savedUser.getLastLogin(),
        savedUser.getToken(),
        savedUser.isActive(),
        phoneDTOs);
    }

    @Override
    @Transactional // Necessary because we update the user (lastLogin, token)
    public LoginResponseDTO loginUser(String bearerToken) {
        // 1. Extract the token
        String token = extractToken(bearerToken);
        if (token == null || !tokenProvider.validateToken(token)) {
            throw new InvalidTokenException("Token inválido o expirado.");
        }

        // 2. Get email from token
        String email = tokenProvider.getEmailFromJWT(token);

        // 3. Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // 4. Verify if the token matches (good practice if you store the last active token)
        if (!user.getToken().equals(token)) {
            throw new InvalidTokenException("El token proporcionado no coincide con el último token activo.");
        }

        // 5. Update last login
        user.setLastLogin(LocalDateTime.now());

        // 6. Generate a new token
        String newToken = tokenProvider.generateToken(user.getEmail());
        user.setToken(newToken);

        // 7. Save the changes in the user
        User updatedUser = userRepository.save(user);

        // 8. Map to LoginResponseDTO
        return mapUserToLoginResponse(updatedUser);
    }

    // Helper methods
    private LoginResponseDTO mapUserToLoginResponse(User user) {
        List<PhoneDTO> phoneDTOs = user.getPhones().stream()
            .map(phone -> {
                PhoneDTO dto = new PhoneDTO();
                dto.setNumber(phone.getNumber());
                dto.setCityCode(phone.getCityCode());
                dto.setCountryCode(phone.getCountryCode());
                return dto;
            })
            .collect(Collectors.toList());

        return new LoginResponseDTO(
            user.getId(),
            user.getCreated(),
            user.getLastLogin(),
            user.getToken(),
            user.isActive(),
            user.getName(),
            user.getEmail(),
            phoneDTOs
        );
    }



    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
