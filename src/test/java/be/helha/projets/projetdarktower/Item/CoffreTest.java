package be.helha.projets.projetdarktower.Item;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoffreTest {

    private Coffre coffre;

    @BeforeEach
    void setUp() {
        coffre = new Coffre("Coffre test", 30.0);
    }

    @Test
    void testConstructeurAvecParametres() {
        assertEquals("Coffre test", coffre.getNom());
        assertEquals("Coffre", coffre.getType());
        assertEquals(30.0, coffre.getChanceDeDrop());
        assertNotNull(coffre.getContenu());
        assertTrue(coffre.getContenu().isEmpty());
    }

    @Test
    void testConstructeurParDefaut() {
        Coffre coffreDefaut = new Coffre();
        assertEquals("Coffre", coffreDefaut.getType());
        assertNotNull(coffreDefaut.getContenu());
        assertTrue(coffreDefaut.getContenu().isEmpty());
    }

    // Si tu as une méthode ajouterItem dans Coffre, teste-la, sinon ignore ce test ou crée-la
    @Test
    void testAjouterItem() {
        Potion potion = new Potion("Petite potion", 20, 50.0, 1);
        assertTrue(coffre.ajouterItem(potion));  // cette méthode doit exister
        List<Item> contenu = coffre.getContenu();
        assertEquals(1, contenu.size());
        assertEquals(potion, contenu.get(0));
    }

    @Test
    void testCapaciteMax() {
        for (int i = 0; i < 10; i++) {
            assertTrue(coffre.ajouterItem(new Potion("Potion " + i, 10, 10.0, 1)));
        }
        assertTrue(coffre.estPlein());
        assertFalse(coffre.ajouterItem(new Potion("Trop", 10, 10.0, 1)));
    }

    // Si tu as une méthode retirerItem(index), teste-la, sinon ignore ou crée-la
    @Test
    void testRetirerItem() {
        Weapon weapon = new Weapon("Epée", 15, 10.0, 2);
        coffre.ajouterItem(weapon);
        assertTrue(coffre.retirerItem(0));
        assertTrue(coffre.estVide());
    }

    @Test
    void testRetirerItemIndexInvalide() {
        assertFalse(coffre.retirerItem(0));
        assertFalse(coffre.retirerItem(-1));
    }

    @Test
    void testEstVideEtEstPlein() {
        assertTrue(coffre.estVide());
        assertFalse(coffre.estPlein());

        for (int i = 0; i < 10; i++) {
            coffre.ajouterItem(new Weapon("Arme " + i, 5, 5.0, 1));
        }

        assertFalse(coffre.estVide());
        assertTrue(coffre.estPlein());
    }

    @Test
    void testToString() {
        coffre.ajouterItem(new Potion("Potion magique", 50, 15.0, 1));
        String output = coffre.toString();
        assertTrue(output.contains("Potion magique"));
        assertTrue(output.contains("Coffre"));
    }

    @Test
    void testToDocument() {
        Potion potion = new Potion("Vie+", 50, 20.0, 1);
        Weapon weapon = new Weapon("Hache", 30, 15.0, 2);
        coffre.ajouterItem(potion);
        coffre.ajouterItem(weapon);

        Document doc = coffre.toDocument();

        assertEquals(coffre.getId(), doc.getString("_id"));
        assertEquals("Coffre", doc.getString("type"));
        assertEquals("Coffre test", doc.getString("nom"));

        List<Document> contenu = (List<Document>) doc.get("contenu");
        assertEquals(2, contenu.size());

        assertEquals("Vie+", contenu.get(0).getString("nom"));
        assertEquals("Hache", contenu.get(1).getString("nom"));
    }
}
