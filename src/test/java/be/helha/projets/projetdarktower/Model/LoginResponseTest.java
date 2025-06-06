package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void testConstructeurEtGetters() {
        Long userId = 123L;
        String username = "userTest";
        String token = "token123";
        int IsLoggedIn = 0;

        LoginResponse response = new LoginResponse(userId, username, token,IsLoggedIn);

        assertEquals(userId, response.getUserId());
        assertEquals(username, response.getUsername());
        assertEquals(token, response.getToken());
        assertEquals(IsLoggedIn, response.getIsLoggedIn());
    }
}
