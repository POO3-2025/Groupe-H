package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WaterWaTest {

    @Test
    void testConstructeur() {
        WaterWa waterWa = new WaterWa("id1");
        assertEquals("WaterWa", waterWa.getNom());
        assertEquals(180, waterWa.getPointsDeVie());
        assertEquals(20, waterWa.getAttaque());
    }

    @Test
    void testAttaquer() {
        WaterWa attacker = new WaterWa("attacker");
        JoWind cible = new JoWind("cible");

        int degats = attacker.attaquer(cible);

        assertEquals(20, degats);
        assertEquals(110 - 20, cible.getPointsDeVie());
    }

    @Test
    void testResetPointDeVie() {
        WaterWa waterWa = new WaterWa("id2");
        waterWa.setPointsDeVie(50);
        waterWa.resetPointDeVie();
        assertEquals(180, waterWa.getPointsDeVie());
    }

    @Test
    void testSetPointsDeVieLimite() {
        WaterWa waterWa = new WaterWa("id3");

        waterWa.setPointsDeVie(200); // au-dessus de la limite
        assertEquals(180, waterWa.getPointsDeVie());

        waterWa.setPointsDeVie(150); // sous la limite
        assertEquals(150, waterWa.getPointsDeVie());
    }
}
