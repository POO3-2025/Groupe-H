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
        assertTrue(coffre.getContenu().isEmpty());
    }

    @Test
    void testConstructeurParDefaut() {
        Coffre coffreDefaut = new Coffre();
        assertEquals("Coffre", coffreDefaut.getType());
        assertNotNull(coffreDefaut.getContenu());
        assertTrue(coffreDefaut.getContenu().isEmpty());
    }

    @Test
    void testAjouterItem() {
        Item potion = new Potion("Petite potion", 20, 50.0, 1);
        assertTrue(coffre.ajouterItem(potion));
        assertEquals(1, coffre.getContenu().size());
        assertEquals(potion, coffre.getContenu().get(0));
    }

    @Test
    void testAjouterCoffreRefuse() {
        Item autreCoffre = new Coffre("Coffre interdit", 0.0);
        assertFalse(coffre.ajouterItem(autreCoffre));
        assertTrue(coffre.getContenu().isEmpty());
    }

    @Test
    void testCapaciteMax() {
        for (int i = 0; i < 10; i++) {
            assertTrue(coffre.ajouterItem(new Potion("Potion " + i, 10, 10.0, 1)));
        }
        assertTrue(coffre.estPlein());
        assertFalse(coffre.ajouterItem(new Potion("Trop", 10, 10.0, 1)));
    }

    @Test
    void testRetirerItem() {
        Item weapon = new Weapon("Epée", 15, 10.0, 2);
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
