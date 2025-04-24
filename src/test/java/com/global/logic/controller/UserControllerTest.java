package com.global.logic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.logic.dtos.LoginResponseDTO;
import com.global.logic.dtos.SignUpRequestDTO;
import com.global.logic.dtos.UserResponseDTO;
import com.global.logic.dtos.PhoneDTO;
import java.util.List;
import com.global.logic.handler.GlobalExceptionHandler;
import com.global.logic.service.UserService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private ObjectMapper objectMapper;
    private UserController userController;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Manually create mocks and dependencies
        userService = Mockito.mock(UserService.class);
        userController = new UserController();
        objectMapper = new ObjectMapper();

        // Use reflection to set the private userService field
        Field userServiceField = UserController.class.getDeclaredField("userService");
        userServiceField.setAccessible(true);
        userServiceField.set(userController, userService);

        // Set up MockMvc with the controller directly, no Spring context
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /sign-up - invalid email")
    void signUp_invalidEmail() throws Exception {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("Juan Perez");
        request.setEmail("invalid-email"); // Invalid email
        request.setPassword("Aaaaaaa1b2"); // Valid password: 1 uppercase, 2 digits, 8+ chars, only letters/digits

        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isArray())
                .andExpect(jsonPath("$.error[0].detail").value(Matchers.containsString("email")));
    }

    @Test
    @DisplayName("POST /sign-up - invalid password")
    void signUp_invalidPassword() throws Exception {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("Juan Perez");
        request.setEmail("juan.perez@example.com");
        request.setPassword("short");

        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isArray())
                .andExpect(jsonPath("$.error[0].detail").value(Matchers.containsString("password")));
    }

    @Test
    @DisplayName("POST /sign-up - success")
    void signUp_success() throws Exception {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("Juan Perez");
        request.setEmail("juan.perez@example.com");
        request.setPassword("Aaaaaaa1b2");

        PhoneDTO phone = new PhoneDTO();
        phone.setNumber(123456789L);
        phone.setCityCode(1);
        phone.setCountryCode("57");
        request.setPhones(List.of(phone));

        UserResponseDTO response = new UserResponseDTO();
        response.setToken("fake.jwt.token");
        response.setActive(true);
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setNumber(123456789L);
        phoneDTO.setCityCode(1);
        phoneDTO.setCountryCode("57");
        response.setPhones(List.of(phoneDTO));

        Mockito.when(userService.registerUser(any(SignUpRequestDTO.class)))
                .thenReturn(response);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("fake.jwt.token"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.phones[0].number").value(123456789))
                .andExpect(jsonPath("$.phones[0].cityCode").value(1))
                .andExpect(jsonPath("$.phones[0].countryCode").value("57"));
    }

    @Test
    @DisplayName("POST /login - success")
    void login_success() throws Exception {
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken("fake.jwt.token");
        response.setEmail("juan.perez@example.com");

        Mockito.when(userService.loginUser(anyString())).thenReturn(response);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/login")
                                .header("Authorization", "Bearer fake.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake.jwt.token"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"));
    }
}
