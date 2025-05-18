package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class PersonnageTest {

    private Personnage personnage;

    @BeforeEach
    void setUp() {
        // Classe anonyme de test
        personnage = new Personnage("ID123", "Testo", 100, 20) {
            @Override
            public int attaquer(Personnage cible) {
                return 0; // Comportement fictif
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
    @DisplayName("1: Test des getters")
    void testGetters() {
        assertEquals("ID123", personnage.getId());
        assertEquals("Testo", personnage.getNom());
        assertEquals(100, personnage.getPointsDeVie());
        assertEquals(20, personnage.getAttaque());
    }

    @Test
    @DisplayName("2: Test des setters")
    void testSetters() {
        personnage.setNom("NouveauNom");
        assertEquals("NouveauNom", personnage.getNom());

        personnage.setPointsDeVie(80);
        assertEquals(80, personnage.getPointsDeVie());

        personnage.setAttaque(35);
        assertEquals(35, personnage.getAttaque());
    }

    @Test
    @DisplayName("3: Test reset des points de vie")
    void testResetPointDeVie() {
        personnage.setPointsDeVie(50);
        personnage.resetPointDeVie();
        assertEquals(100, personnage.getPointsDeVie(), "resetPointDeVie() doit remettre les PV à 100");
    }
}
