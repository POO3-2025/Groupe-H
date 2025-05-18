package be.helha.projets.projetdarktower.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class PotionTest {

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Constructeur avec paramètres")
    void testConstructeurAvecParametres() {
        Potion potion = new Potion("Potion de soin de base", 25, 20.0, 1);

        assertEquals("Potion de soin de base", potion.getNom());
        assertEquals("Potion", potion.getType());
        assertEquals(25, potion.getPointsDeVieRecuperes());
        assertEquals(1, potion.getUsages());
        assertEquals(20.0, potion.getChanceDeDrop());
        assertNotNull(potion.getId());
    }

    @Test
    @DisplayName("2: Constructeur par défaut")
    void testConstructeurParDefaut() {
        Potion potion = new Potion();

        assertEquals("Potion", potion.getType());
        assertNull(potion.getNom());
        assertNull(potion.getId());
        assertEquals(0, potion.getPointsDeVieRecuperes());
        assertEquals(0, potion.getUsages());
    }

    @Test
    @DisplayName("3: Getters et setters")
    void testGettersEtSetters() {
        Potion potion = new Potion();
        potion.setNom("Super Potion");
        potion.setPointsDeVieRecuperes(75);
        potion.setUsages(2);
        potion.setChanceDeDrop(5.5);
        potion.setId("xyz789");

        assertEquals("Super Potion", potion.getNom());
        assertEquals(75, potion.getPointsDeVieRecuperes());
        assertEquals(2, potion.getUsages());
        assertEquals(5.5, potion.getChanceDeDrop());
        assertEquals("xyz789", potion.getId());
    }

    @Test
    @DisplayName("4: toString()")
    void testToString() {
        Potion potion = new Potion("Total soin", 100, 1.0, 1);
        String result = potion.toString();

        assertTrue(result.contains("Potion"));
        assertTrue(result.contains("Total soin"));
        assertTrue(result.contains("pointsDeVieRecuperes=100"));
    }
}
