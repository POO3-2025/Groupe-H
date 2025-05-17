package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void testGetters() {
        assertEquals("ID123", personnage.getId());
        assertEquals("Testo", personnage.getNom());
        assertEquals(100, personnage.getPointsDeVie());
        assertEquals(20, personnage.getAttaque());
    }

    @Test
    void testSetters() {
        personnage.setNom("NouveauNom");
        assertEquals("NouveauNom", personnage.getNom());

        personnage.setPointsDeVie(80);
        assertEquals(80, personnage.getPointsDeVie());

        personnage.setAttaque(35);
        assertEquals(35, personnage.getAttaque());
    }

    @Test
    void testResetPointDeVie() {
        personnage.setPointsDeVie(50);
        personnage.resetPointDeVie();
        assertEquals(100, personnage.getPointsDeVie(), "resetPointDeVie() doit remettre les PV à 100");
    }
}
