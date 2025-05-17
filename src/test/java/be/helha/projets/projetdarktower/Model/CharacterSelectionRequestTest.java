package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CharacterSelectionRequestTest {

    @Test
    void testGettersAndSetters() {
        CharacterSelectionRequest req = new CharacterSelectionRequest();
        req.setUserId("user123");
        req.setCharacterId("char456");

        assertEquals("user123", req.getUserId());
        assertEquals("char456", req.getCharacterId());
    }
}