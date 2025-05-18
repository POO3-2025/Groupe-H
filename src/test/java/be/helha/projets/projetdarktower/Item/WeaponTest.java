package be.helha.projets.projetdarktower.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class WeaponTest {

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Constructeur avec paramètres")
    void testConstructeurAvecParametres() {
        Weapon weapon = new Weapon("Épée en fer", 40, 4.0, 3);

        assertEquals("Épée en fer", weapon.getNom());
        assertEquals("Weapon", weapon.getType());
        assertEquals(40, weapon.getDegats());
        assertEquals(3, weapon.getUsages());
        assertEquals(4.0, weapon.getChanceDeDrop());
        assertNotNull(weapon.getId());
    }

    @Test
    @DisplayName("2: Constructeur par défaut")
    void testConstructeurParDefaut() {
        Weapon weapon = new Weapon();

        assertEquals("Weapon", weapon.getType());
        assertNull(weapon.getNom());
        assertNull(weapon.getId());
        assertEquals(0, weapon.getDegats());
        assertEquals(0, weapon.getUsages());
    }

    @Test
    @DisplayName("3: Getters et setters")
    void testGettersEtSetters() {
        Weapon weapon = new Weapon();
        weapon.setNom("Hache de guerre");
        weapon.setDegats(55);
        weapon.setUsages(10);
        weapon.setChanceDeDrop(2.5);
        weapon.setId("123abc");

        assertEquals("Hache de guerre", weapon.getNom());
        assertEquals(55, weapon.getDegats());
        assertEquals(10, weapon.getUsages());
        assertEquals(2.5, weapon.getChanceDeDrop());
        assertEquals("123abc", weapon.getId());
    }

    @Test
    @DisplayName("4: toString()")
    void testToString() {
        Weapon weapon = new Weapon("Couteau en diamant", 60, 1.0, 1);
        String result = weapon.toString();

        assertTrue(result.contains("Weapon"));
        assertTrue(result.contains("Couteau en diamant"));
        assertTrue(result.contains("dégâts=60"));
    }
}
