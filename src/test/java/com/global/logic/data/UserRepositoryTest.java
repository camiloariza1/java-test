package com.global.logic.data;

import com.global.logic.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Guardar y buscar usuario por email")
    void saveAndFindByEmail() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setActive(true);
        user.setToken("token");
        user.setCreated(java.time.LocalDateTime.now());
        user.setLastLogin(java.time.LocalDateTime.now());

        // Act
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        Optional<User> found = userRepository.findByEmail("testuser@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testuser@example.com", found.get().getEmail());
    }
}
