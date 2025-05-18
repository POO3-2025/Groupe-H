package be.helha.projets.projetdarktower.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ItemFactoryTest {

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Créer des armes connues")
    void testCreerItemKnownWeapons() {
        Item sword = ItemFactory.creerItem("épée en bois");
        assertNotNull(sword);
        assertTrue(sword instanceof Weapon);
        assertEquals("Weapon", sword.getType());
        assertEquals("Épée en bois", sword.getNom());
        assertTrue(sword.getChanceDeDrop() > 0);
        assertNotNull(sword.getId());

        Item knife = ItemFactory.creerItem("couteau en fer");
        assertNotNull(knife);
        assertTrue(knife instanceof Weapon);
        assertEquals("Weapon", knife.getType());
        assertEquals("Couteau en fer", knife.getNom());
    }

    @Test
    @DisplayName("2: Créer des potions connues")
    void testCreerItemKnownPotions() {
        Item potion = ItemFactory.creerItem("potion de soin intermédiaire");
        assertNotNull(potion);
        assertTrue(potion instanceof Potion);
        assertEquals("Potion", potion.getType());
        assertEquals("Potion de soin intermédiaire", potion.getNom());
    }

    @Test
    @DisplayName("3: Créer un coffre")
    void testCreerItemCoffre() {
        Item coffre = ItemFactory.creerItem("coffre");
        assertNotNull(coffre);
        assertTrue(coffre instanceof Coffre);
        assertEquals("Coffre", coffre.getNom());
        assertEquals("Coffre", coffre.getType());
    }

    @Test
    @DisplayName("4: Créer un item inconnu lance exception")
    void testCreerItemUnknown() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ItemFactory.creerItem("objet inconnu");
        });
        assertEquals("Objet inconnu: objet inconnu", exception.getMessage());
    }

    @Test
    @DisplayName("5: Vérifier la présence des clés importantes dans getAllItems()")
    void testGetAllItemsContainsExpectedKeys() {
        Map<String, Item> allItems = ItemFactory.getAllItems();
        assertNotNull(allItems);

        assertTrue(allItems.containsKey("épée en bois"));
        assertTrue(allItems.containsKey("potion de soin de base"));
        assertTrue(allItems.containsKey("coffre"));

        assertTrue(allItems.get("épée en bois") instanceof Weapon);
        assertTrue(allItems.get("potion de soin de base") instanceof Potion);
        assertTrue(allItems.get("coffre") instanceof Coffre);
    }
}
