package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoWindTest {

    private JoWind joWind;
    private DummyCible cible;

    @BeforeEach
    void setUp() {
        joWind = new JoWind("id_jo");
        cible = new DummyCible("cible", 100);
    }

    @Test
    void testInitialisation() {
        assertEquals("JoWind", joWind.getNom());
        assertEquals(110, joWind.getPointsDeVie());
        assertEquals(30, joWind.getAttaque());
    }

    @Test
    void testResetPointDeVie() {
        joWind.setPointsDeVie(20);
        joWind.resetPointDeVie();
        assertEquals(110, joWind.getPointsDeVie());
    }

    @Test
    void testSetPointsDeVieLimite() {
        joWind.setPointsDeVie(150);
        assertEquals(110, joWind.getPointsDeVie(), "Les PV ne doivent pas dépasser 110");

        joWind.setPointsDeVie(95);
        assertEquals(95, joWind.getPointsDeVie());
    }

    @Test
    void testAttaquer() {
        int pvAvant = cible.getPointsDeVie();
        int degatsInfliges = joWind.attaquer(cible);

        assertEquals(30, degatsInfliges);
        assertEquals(pvAvant - 30, cible.getPointsDeVie());
    }

    // Classe factice pour cible d'attaque
    static class DummyCible extends Personnage {
        public DummyCible(String id, int pointsDeVie) {
            super(id, "Dummy", pointsDeVie, 0);
        }

        @Override
        public int attaquer(Personnage cible) {
            return 0;
        }

        @Override
        public void resetPointDeVie() {
            this.setPointsDeVie(100);
        }
    }
}
