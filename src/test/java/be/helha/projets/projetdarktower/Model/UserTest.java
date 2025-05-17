package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGettersEtSetters() {
        User user = new User();

        Long id = 42L;
        String username = "testUser";
        String password = "secret";

        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);

        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
    }
}
