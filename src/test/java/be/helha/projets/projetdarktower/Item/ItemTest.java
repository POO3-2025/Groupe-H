package be.helha.projets.projetdarktower.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    // Classe concrète pour test : simule un objet réel basé sur Item
    static class DummyItem extends Item {
        public DummyItem(String nom, double chanceDeDrop) {
            super(nom, chanceDeDrop);
            this.type = "Dummy";
        }
    }

    @Test
    @DisplayName("1: Test constructeur et getters")
    void testConstructeurEtGetters() {
        Item item = new DummyItem("Pierre magique", 55.5);

        assertNotNull(item.getId()); // L'ID est généré avec ObjectId
        assertEquals("Pierre magique", item.getNom());
        assertEquals("Dummy", item.getType());
        assertEquals(55.5, item.getChanceDeDrop());
    }

    @Test
    @DisplayName("2: Test setters")
    void testSetters() {
        Item item = new DummyItem("Initial", 0.0);

        item.setNom("Amulette");
        item.setType("Accessoire");
        item.setChanceDeDrop(80.0);
        item.setId("abc123");

        assertEquals("Amulette", item.getNom());
        assertEquals("Accessoire", item.getType());
        assertEquals(80.0, item.getChanceDeDrop());
        assertEquals("abc123", item.getId());
    }

    @Test
    @DisplayName("3: Test toString()")
    void testToString() {
        Item item = new DummyItem("Étoile filante", 100.0);
        String result = item.toString();

        assertTrue(result.contains("Étoile filante"));
        assertTrue(result.contains("Dummy"));
        assertTrue(result.contains("chanceDeDrop=100.0"));
    }

    @Test
    @DisplayName("4: Test constructeur par défaut")
    void testConstructeurParDefaut() {
        Item item = new Item() {}; // Classe anonyme pour test Jackson
        assertNull(item.getId());
        assertNull(item.getNom());
        assertNull(item.getType());
        assertEquals(0.0, item.getChanceDeDrop());
    }
}
