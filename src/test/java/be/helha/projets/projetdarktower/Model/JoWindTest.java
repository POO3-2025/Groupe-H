package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

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

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Test du constructeur")
    void testConstructeur() {
        assertEquals("J001", joWind.getId());
        assertEquals("JoWind", joWind.getNom());
        assertEquals(110, joWind.getPointsDeVie());
        assertEquals(30, joWind.getAttaque());
    }

    @Test
    @DisplayName("2: Test de la méthode attaquer")
    void testAttaquer() {
        int degats = joWind.attaquer(cible);
        assertEquals(30, degats);
        assertEquals(70, cible.getPointsDeVie()); // 100 - 30
    }

    @Test
    @DisplayName("3: Test de reset des points de vie")
    void testResetPointDeVie() {
        joWind.setPointsDeVie(50);
        assertEquals(50, joWind.getPointsDeVie());

        joWind.resetPointDeVie();
        assertEquals(110, joWind.getPointsDeVie());
    }

    @Test
    @DisplayName("4: Test limite max des points de vie")
    void testSetPointsDeVieMax() {
        joWind.setPointsDeVie(150);
        assertEquals(110, joWind.getPointsDeVie()); // ne dépasse pas 110
    }

    @Test
    @DisplayName("5: Test setter points de vie normal")
    void testSetPointsDeVieNormal() {
        joWind.setPointsDeVie(80);
        assertEquals(80, joWind.getPointsDeVie());
    }
}
