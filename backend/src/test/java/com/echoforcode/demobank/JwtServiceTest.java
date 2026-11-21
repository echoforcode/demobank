package com.echoforcode.demobank;

import com.echoforcode.demobank.service.JwtService;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    // 32-byte signing key. Never use a real application secret in tests.
    private static final String TEST_SECRET = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef".getBytes());

    @Test
    void generateToken_canValidateAndExtractSubject() {
        JwtService service = new JwtService(TEST_SECRET, 60_000);

        String token = service.generateToken("alice@example.com");

        assertTrue(service.isTokenValid(token));
        assertEquals("alice@example.com", service.extractSubject(token));
        assertTrue(service.extractExpiration(token).getTime() > System.currentTimeMillis());
    }

    @Test
    void generateToken_rejectsBlankUsername() {
        JwtService service = new JwtService(TEST_SECRET, 60_000);
        assertThrows(IllegalArgumentException.class, () -> service.generateToken("  "));
        assertThrows(IllegalArgumentException.class, () -> service.generateToken(null));
    }

    @Test
    void tokenWithDifferentKey_isInvalid() {
        JwtService issuer = new JwtService(TEST_SECRET, 60_000);
        String otherSecret = Base64.getEncoder()
                .encodeToString("abcdef0123456789abcdef0123456789".getBytes());
        JwtService verifier = new JwtService(otherSecret, 60_000);

        assertFalse(verifier.isTokenValid(issuer.generateToken("alice@example.com")));
    }

    @Test
    void malformedToken_isInvalid() {
        JwtService service = new JwtService(TEST_SECRET, 60_000);
        assertFalse(service.isTokenValid("not-a-jwt"));
    }

    @Test
    void constructor_rejectsNonPositiveExpiration() {
        assertThrows(IllegalArgumentException.class,
                () -> new JwtService(TEST_SECRET, 0));
    }
}
