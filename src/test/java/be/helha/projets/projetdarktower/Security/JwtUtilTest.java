package be.helha.projets.projetdarktower.Security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateToken_shouldCreateValidToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Le token doit contenir le username encodé (en base64) - test basique
        assertTrue(token.contains("."));
    }

    @Test
    void getUsernameFromJwtToken_shouldReturnCorrectUsername() {
        String username = "user123";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.getUsernameFromJwtToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateJwtToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("validUser");
        assertTrue(jwtUtil.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtUtil.validateJwtToken(invalidToken));
    }
}
