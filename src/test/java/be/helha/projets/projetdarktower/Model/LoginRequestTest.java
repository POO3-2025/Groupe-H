package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testConstructeurParDefautEtSetters() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");

        assertEquals("testuser", request.getUsername());
        assertEquals("testpass", request.getPassword());
    }

    @Test
    void testConstructeurAvecParametres() {
        LoginRequest request = new LoginRequest("admin", "secure123");

        assertEquals("admin", request.getUsername());
        assertEquals("secure123", request.getPassword());
    }

    @Test
    void testModificationsDesChamps() {
        LoginRequest request = new LoginRequest("john", "1234");
        request.setUsername("doe");
        request.setPassword("5678");

        assertEquals("doe", request.getUsername());
        assertEquals("5678", request.getPassword());
    }
}
