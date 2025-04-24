package com.global.logic.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {
    @Test
    void testPasswordEncoder() {
        SecurityConfig config = new SecurityConfig();
        assertNotNull(config.passwordEncoder());
    }
}
