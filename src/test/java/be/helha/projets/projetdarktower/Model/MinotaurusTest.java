package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

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

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Test d'initialisation des points de vie et attaque")
    void testInitialisation() {
        int expectedPV = 80 + (int)(Math.pow(3, 1.5) * 3.5);
        int expectedAtk = 20 + (int)(Math.pow(3, 1.3) * 2.5);
        assertEquals(expectedPV, minotaurus.getPointsDeVie());
        assertEquals(expectedAtk, minotaurus.getAttaque());
        assertEquals(3, minotaurus.getNiveau());
    }

    @Test
    @DisplayName("2: Test setter du niveau")
    void testSetNiveau() {
        minotaurus.setNiveau(5);
        assertEquals(5, minotaurus.getNiveau());
    }

    @Test
    @DisplayName("3: Test reset des points de vie")
    void testResetPointsDeVie() {
        minotaurus.setPointsDeVie(10);
        minotaurus.resetPointsDeVie();
        int expectedPV = 80 + (int)(Math.pow(minotaurus.getNiveau(), 1.5) * 3.5);
        assertEquals(expectedPV, minotaurus.getPointsDeVie());
    }

    @Test
    @DisplayName("4: Attaque sur cible neutre")
    void testAttaquerCibleNeutre() {
        int pvAvant = cibleNeutre.getPointsDeVie();
        int degats = minotaurus.attaquer(cibleNeutre);

        assertEquals(minotaurus.getAttaque(), degats);
        assertEquals(pvAvant - degats, cibleNeutre.getPointsDeVie());
    }

    @Test
    @DisplayName("5: Attaque sur WaterWa (dégâts divisés par 2)")
    void testAttaquerWaterWa() {
        int pvAvant = waterWa.getPointsDeVie();
        int degats = minotaurus.attaquer(waterWa);

        assertEquals(minotaurus.getAttaque() / 2, degats);
        assertEquals(pvAvant - degats, waterWa.getPointsDeVie());
    }

    @RepeatedTest(20)
    @DisplayName("6: Attaque sur JoWind avec esquive possible")
    void testAttaquerJoWindAvecEsquivePossible() {
        int pvAvant = joWind.getPointsDeVie();
        int degats = minotaurus.attaquer(joWind);

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
