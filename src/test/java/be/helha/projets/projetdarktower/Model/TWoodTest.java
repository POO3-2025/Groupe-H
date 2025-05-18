package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class TWoodTest {

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Test constructeur")
    void testConstructeur() {
        TWood tWood = new TWood("id1");
        assertNotNull(tWood);
        assertEquals("id1", tWood.getId());
        assertEquals("TWood", tWood.getNom());
        assertEquals(90, tWood.getPointsDeVie());
        assertEquals(25, tWood.getAttaque());
    }

    @Test
    @DisplayName("2: Test setters et getters")
    void testSettersEtGetters() {
        TWood tWood = new TWood("id2");
        tWood.setNom("NomTest");
        assertEquals("NomTest", tWood.getNom());

        tWood.setPointsDeVie(100); // Au-dessus max, doit limiter à 90
        assertEquals(90, tWood.getPointsDeVie());

        tWood.setPointsDeVie(50);
        assertEquals(50, tWood.getPointsDeVie());
    }

    @Test
    @DisplayName("3: Test méthode attaquer")
    void testAttaquer() {
        TWood tWood = new TWood("id3");
        Personnage cible = new TWood("idCible");
        cible.setPointsDeVie(50);

        int degats = tWood.attaquer(cible);

        assertEquals(25, degats, "L'attaque doit infliger 25 dégâts");
        assertEquals(25, cible.getPointsDeVie(), "La cible doit avoir 25 PV restants");
    }

    @Test
    @DisplayName("4: Test régénération des points de vie")
    void testRegenererPV() {
        TWood tWood = new TWood("id4");
        tWood.setPointsDeVie(60);

        tWood.regenererPV();

        assertEquals(75, tWood.getPointsDeVie(), "La régénération doit augmenter les PV de 15");

        tWood.regenererPV();
        tWood.regenererPV();

        assertEquals(90, tWood.getPointsDeVie(), "Les PV ne doivent pas dépasser 90");
    }
}
