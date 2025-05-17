package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FistFireTest {

    private FistFire fistFire;
    private DummyCible cible;

    @BeforeEach
    void setUp() {
        fistFire = new FistFire("id_fire");
        cible = new DummyCible("cible", 100);
    }

    @Test
    void testInitialisation() {
        assertEquals("Fist Fire", fistFire.getNom());
        assertEquals(70, fistFire.getPointsDeVie());
        assertEquals(40, fistFire.getAttaque());
    }

    @Test
    void testResetPointDeVie() {
        fistFire.setPointsDeVie(10);
        fistFire.resetPointDeVie();
        assertEquals(70, fistFire.getPointsDeVie());
    }

    @Test
    void testSetPointsDeVieMaxLimite() {
        fistFire.setPointsDeVie(100);
        assertEquals(70, fistFire.getPointsDeVie(), "Les PV ne doivent pas dépasser 70");

        fistFire.setPointsDeVie(55);
        assertEquals(55, fistFire.getPointsDeVie(), "Les PV peuvent être fixés sous la limite");
    }

    @RepeatedTest(10)
    void testAttaqueAvecOuSansCritique() {
        // Lancer l'attaque plusieurs fois pour couvrir les deux cas (critique et normal)
        int pvAvant = cible.getPointsDeVie();
        int degatsInfliges = fistFire.attaquer(cible);

        // Vérifie que les dégâts sont soit 40 soit 80
        assertTrue(degatsInfliges == 40 || degatsInfliges == 80,
                "Dégâts doivent être 40 (normal) ou 80 (critique)");

        // Vérifie que les PV de la cible ont bien été réduits
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
