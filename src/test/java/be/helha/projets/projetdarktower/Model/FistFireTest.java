package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class FistFireTest {

    private FistFire fistFire;
    private DummyCible cible;

    @BeforeEach
    void setUp() {
        fistFire = new FistFire("id_fire");
        cible = new DummyCible("cible", 100);
    }

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Test de l'initialisation de FistFire")
    void testInitialisation() {
        assertEquals("Fist Fire", fistFire.getNom());
        assertEquals(150, fistFire.getPointsDeVie());
        assertEquals(2000, fistFire.getAttaque());
    }

    @Test
    @DisplayName("2: Test reset des points de vie")
    void testResetPointDeVie() {
        fistFire.setPointsDeVie(10);
        fistFire.resetPointDeVie();
        assertEquals(150, fistFire.getPointsDeVie());
    }

    @Test
    @DisplayName("3: Test limite max des points de vie")
    void testSetPointsDeVieMaxLimite() {
        fistFire.setPointsDeVie(200);
        assertEquals(150, fistFire.getPointsDeVie(), "Les PV ne doivent pas dépasser 150");

        fistFire.setPointsDeVie(55);
        assertEquals(55, fistFire.getPointsDeVie(), "Les PV peuvent être fixés sous la limite");
    }

    @RepeatedTest(10)
    @DisplayName("4: Test attaque avec ou sans critique")
    void testAttaqueAvecOuSansCritique() {
        int pvAvant = cible.getPointsDeVie();
        int degatsInfliges = fistFire.attaquer(cible);

        assertTrue(degatsInfliges == 2000 || degatsInfliges == 4000,
                "Dégâts doivent être 60 (normal) ou 120 (critique)");

        assertEquals(pvAvant - degatsInfliges, cible.getPointsDeVie());
    }

    // Classe factice pour tester les attaques
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
            this.pointsDeVie = 100;
        }
    }
}
