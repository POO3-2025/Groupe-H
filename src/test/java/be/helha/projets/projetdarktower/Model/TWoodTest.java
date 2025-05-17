package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TWoodTest {

    @Test
    void testConstructeur() {
        TWood tWood = new TWood("id1");
        assertNotNull(tWood);
        assertEquals("id1", tWood.getId());
    }

    @Test
    void testSettersEtGetters() {
        TWood tWood = new TWood("id2");
        tWood.setNom("NomTest");
        assertEquals("NomTest", tWood.getNom());
    }

    @Test
    void testLogiqueMetier() {
        TWood tWood = new TWood("id3");
        // Ajoute ici des assertions selon la logique de ta classe
    }
}