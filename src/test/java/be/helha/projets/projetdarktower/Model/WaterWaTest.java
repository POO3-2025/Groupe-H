package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class WaterWaTest {

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Test constructeur")
    void testConstructeur() {
        WaterWa waterWa = new WaterWa("id1");
        assertEquals("WaterWa", waterWa.getNom());
        assertEquals(220, waterWa.getPointsDeVie());
        assertEquals(30, waterWa.getAttaque());
    }

    @Test
    @DisplayName("2: Test méthode attaquer")
    void testAttaquer() {
        WaterWa attacker = new WaterWa("attacker");
        JoWind cible = new JoWind("cible");

        int degats = attacker.attaquer(cible);

        assertEquals(30, degats);
        assertEquals(110 - 30, cible.getPointsDeVie());
    }

    @Test
    @DisplayName("3: Test reset des points de vie")
    void testResetPointDeVie() {
        WaterWa waterWa = new WaterWa("id2");
        waterWa.setPointsDeVie(50);
        waterWa.resetPointDeVie();
        assertEquals(220, waterWa.getPointsDeVie());
    }

    @Test
    @DisplayName("4: Test limite des points de vie")
    void testSetPointsDeVieLimite() {
        WaterWa waterWa = new WaterWa("id3");

        waterWa.setPointsDeVie(240); // au-dessus de la limite
        assertEquals(220, waterWa.getPointsDeVie());

        waterWa.setPointsDeVie(150); // sous la limite
        assertEquals(150, waterWa.getPointsDeVie());
    }
}
