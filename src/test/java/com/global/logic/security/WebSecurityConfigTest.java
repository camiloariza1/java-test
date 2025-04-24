package com.global.logic.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebSecurityConfigTest {
    @Test
    void testFilterChain() throws Exception {
        WebSecurityConfig config = new WebSecurityConfig();
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        // We only check that the bean can be created without exception
        assertNotNull(config.filterChain(http));
    }
}
