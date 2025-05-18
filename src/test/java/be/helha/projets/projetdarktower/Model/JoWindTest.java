package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoWindTest {

    private JoWind joWind;
    private Personnage cible;

    @BeforeEach
    void setUp() {
        joWind = new JoWind("J001");
        // Personnage cible fictif (classe anonyme) pour tester
        cible = new Personnage("C001", "Cible", 100, 20) {
            @Override
            public int attaquer(Personnage p) {
                return 0; // inutile ici
            }

            @Override
            public void resetPointDeVie() {
                this.setPointsDeVie(100);
            }
        };
    }

    @Test
    void testConstructeur() {
        assertEquals("J001", joWind.getId());
        assertEquals("JoWind", joWind.getNom());
        assertEquals(110, joWind.getPointsDeVie());
        assertEquals(30, joWind.getAttaque());
    }

    @Test
    void testAttaquer() {
        int degats = joWind.attaquer(cible);
        assertEquals(30, degats);
        assertEquals(70, cible.getPointsDeVie()); // 100 - 30
    }

    @Test
    void testResetPointDeVie() {
        joWind.setPointsDeVie(50);
        assertEquals(50, joWind.getPointsDeVie());

        joWind.resetPointDeVie();
        assertEquals(110, joWind.getPointsDeVie());
    }

    @Test
    void testSetPointsDeVieMax() {
        joWind.setPointsDeVie(150);
        assertEquals(110, joWind.getPointsDeVie()); // ne dépasse pas 110
    }

    @Test
    void testSetPointsDeVieNormal() {
        joWind.setPointsDeVie(80);
        assertEquals(80, joWind.getPointsDeVie());
    }
}
