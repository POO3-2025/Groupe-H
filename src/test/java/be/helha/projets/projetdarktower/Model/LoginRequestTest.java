package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Constructeur par défaut et setters")
    void testConstructeurParDefautEtSetters() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");

        assertEquals("testuser", request.getUsername());
        assertEquals("testpass", request.getPassword());
    }

    @Test
    @DisplayName("2: Constructeur avec paramètres")
    void testConstructeurAvecParametres() {
        LoginRequest request = new LoginRequest("admin", "secure123");

        assertEquals("admin", request.getUsername());
        assertEquals("secure123", request.getPassword());
    }

    @Test
    @DisplayName("3: Modification des champs")
    void testModificationsDesChamps() {
        LoginRequest request = new LoginRequest("john", "1234");
        request.setUsername("doe");
        request.setPassword("5678");

        assertEquals("doe", request.getUsername());
        assertEquals("5678", request.getPassword());
    }
}
