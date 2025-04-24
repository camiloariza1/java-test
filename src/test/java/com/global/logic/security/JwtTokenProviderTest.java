package com.global.logic.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    @Test
    void testInitAndGenerateToken() {
        JwtTokenProvider provider = new JwtTokenProvider();
        // Generate a secure key for HS512
        javax.crypto.SecretKey secureKey = io.jsonwebtoken.security.Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        String secureKeyString = java.util.Base64.getEncoder().encodeToString(secureKey.getEncoded());
        ReflectionTestUtils.setField(provider, "jwtSecretString", secureKeyString);
        ReflectionTestUtils.setField(provider, "jwtExpirationInMs", 10000);
        provider.init();
        String token = provider.generateToken("test@email.com");
        assertNotNull(token);
    }
}
