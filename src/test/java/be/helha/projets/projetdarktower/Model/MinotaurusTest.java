package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinotaurusTest {

    private Minotaurus minotaurus;
    private DummyCible cibleNeutre;
    private WaterWa waterWa;
    private JoWind joWind;

    @BeforeEach
    void setUp() {
        minotaurus = new Minotaurus("mino1", 3);
        cibleNeutre = new DummyCible("cible", 100);
        waterWa = new WaterWa("water1");
        joWind = new JoWind("jo1");
    }

    @Test
    void testInitialisation() {
        int expectedPV = 80 + (int)(Math.pow(3, 1.5) * 3.5);
        int expectedAtk = 20 + (int)(Math.pow(3, 1.3) * 2.5);
        assertEquals(expectedPV, minotaurus.getPointsDeVie());
        assertEquals(expectedAtk, minotaurus.getAttaque());
        assertEquals(3, minotaurus.getNiveau());
    }

    @Test
    void testSetNiveau() {
        minotaurus.setNiveau(5);
        assertEquals(5, minotaurus.getNiveau());
    }

    @Test
    void testResetPointsDeVie() {
        minotaurus.setPointsDeVie(10);
        minotaurus.resetPointsDeVie();
        int expectedPV = 80 + (int)(Math.pow(minotaurus.getNiveau(), 1.5) * 3.5);
        assertEquals(expectedPV, minotaurus.getPointsDeVie());
    }

    @Test
    void testAttaquerCibleNeutre() {
        int pvAvant = cibleNeutre.getPointsDeVie();
        int degats = minotaurus.attaquer(cibleNeutre);

        assertEquals(minotaurus.getAttaque(), degats);
        assertEquals(pvAvant - degats, cibleNeutre.getPointsDeVie());
    }

    @Test
    void testAttaquerWaterWa() {
        int pvAvant = waterWa.getPointsDeVie();
        int degats = minotaurus.attaquer(waterWa);

        // Dégats doivent être divisés par 2
        assertEquals(minotaurus.getAttaque() / 2, degats);
        assertEquals(pvAvant - degats, waterWa.getPointsDeVie());
    }

    @RepeatedTest(20)
    void testAttaquerJoWindAvecEsquivePossible() {
        int pvAvant = joWind.getPointsDeVie();
        int degats = minotaurus.attaquer(joWind);

        // Degats soit 0 (esquive) ou attaque normale
        assertTrue(degats == 0 || degats == minotaurus.getAttaque());

        int expectedPV = degats == 0 ? pvAvant : pvAvant - degats;
        assertEquals(expectedPV, joWind.getPointsDeVie());
    }

    // Classe dummy cible neutre pour test
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
