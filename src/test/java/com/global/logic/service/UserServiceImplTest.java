package com.global.logic.service;

import com.global.logic.dtos.PhoneDTO;
import com.global.logic.dtos.SignUpRequestDTO;
import com.global.logic.dtos.UserResponseDTO;
import com.global.logic.model.User;
import com.global.logic.model.Phone;
import com.global.logic.data.UserRepository;
import com.global.logic.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private SignUpRequestDTO signUpRequest;
    private User user;
    private String fakeToken;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequestDTO();
        signUpRequest.setName("Juan Perez");
        signUpRequest.setEmail("juan.perez@example.com");
        signUpRequest.setPassword("Pass1234!");
        signUpRequest.setPhones(Collections.singletonList(
                new PhoneDTO()
        ));
        PhoneDTO phoneDTO = signUpRequest.getPhones().get(0);
        phoneDTO.setNumber(1234567890L);
        phoneDTO.setCityCode(1);
        phoneDTO.setCountryCode("57");

        user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword("encodedPass");
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(user.getCreated());
        user.setPhones(Collections.emptyList());

        fakeToken = "fake.jwt.token";
    }

    @Test
    @DisplayName("Registro exitoso de usuario")
    void registerUser_Success() {
        when(userRepository.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(jwtTokenProvider.generateToken(anyString())).thenReturn(fakeToken);
        User userToSave = new User();
        userToSave.setName(signUpRequest.getName());
        userToSave.setEmail(signUpRequest.getEmail());
        userToSave.setPassword("encodedPass");
        userToSave.setPhones(signUpRequest.getPhones().stream()
                .map(dto -> {
                    Phone phone = new Phone();
                    phone.setNumber(dto.getNumber());
                    phone.setCityCode(dto.getCityCode());
                    phone.setCountryCode(dto.getCountryCode());
                    return phone;
                })
                .collect(Collectors.toList()));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            userArg.setId(UUID.randomUUID());
            userArg.setCreated(LocalDateTime.now());
            userArg.setLastLogin(userArg.getCreated());
            userArg.setActive(true);
            userArg.setToken(fakeToken);
            return userArg;
        });

        // Act
        UserResponseDTO response = userService.registerUser(signUpRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getCreated());
        assertNotNull(response.getLastLogin());
        assertEquals(fakeToken, response.getToken());
        assertTrue(response.isActive());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Registro Falla si Email ya Existe")
    void registerUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail("juan.perez@example.com")).thenReturn(Optional.of(user)); // Simula que el email SÍ
                                                                                            // existe

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(signUpRequest);
        });

        assertEquals("El correo ya registrado: juan.perez@example.com", exception.getMessage());

        // Verificar que save, encode y generateToken NO fueron llamados
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("loginUser - éxito")
    void loginUser_Success() {
        String bearerToken = "Bearer " + fakeToken;
        when(jwtTokenProvider.validateToken(fakeToken)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromJWT(fakeToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(user.getEmail())).thenReturn("new.jwt.token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Set the token to match the fakeToken for the user
        user.setToken(fakeToken);

        var response = userService.loginUser(bearerToken);

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals("new.jwt.token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("loginUser - token inválido")
    void loginUser_InvalidToken() {
        String bearerToken = "Bearer invalid.token";
        when(jwtTokenProvider.validateToken("invalid.token")).thenReturn(false);

        Exception exception = assertThrows(
            com.global.logic.service.InvalidTokenException.class,
            () -> userService.loginUser(bearerToken)
        );
        assertTrue(exception.getMessage().toLowerCase().contains("inválido"));
    }

    @Test
    @DisplayName("loginUser - usuario no encontrado")
    void loginUser_UserNotFound() {
        String bearerToken = "Bearer " + fakeToken;
        when(jwtTokenProvider.validateToken(fakeToken)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromJWT(fakeToken)).thenReturn("notfound@example.com");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(
            com.global.logic.service.UserNotFoundException.class,
            () -> userService.loginUser(bearerToken)
        );
    }

    @Test
    @DisplayName("Registro falla si no tiene nombre")
    void registerUser_FailsWithoutName() {
        signUpRequest.setName(null);
        when(userRepository.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(
            Exception.class,
            () -> userService.registerUser(signUpRequest)
        );
    }

    @Test
    @DisplayName("Registro exitoso sin teléfonos")
    void registerUser_SuccessWithoutPhones() {
        signUpRequest.setPhones(Collections.emptyList());
        when(userRepository.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(jwtTokenProvider.generateToken(anyString())).thenReturn(fakeToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            userArg.setId(UUID.randomUUID());
            userArg.setCreated(LocalDateTime.now());
            userArg.setLastLogin(userArg.getCreated());
            userArg.setActive(true);
            userArg.setToken(fakeToken);
            return userArg;
        });

        UserResponseDTO response = userService.registerUser(signUpRequest);

        assertNotNull(response);
        assertEquals(fakeToken, response.getToken());
        assertTrue(response.isActive());
    }

}

