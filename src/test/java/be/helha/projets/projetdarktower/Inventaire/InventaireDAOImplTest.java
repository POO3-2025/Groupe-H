package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.Item.*;
import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Model.FistFire;
import be.helha.projets.projetdarktower.Model.Minotaurus;
import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Model.WaterWa;
import org.bson.Document;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InventaireDAOImplTest {

    private InventaireDAOImpl inventaireDAO;
    private final int testPersonnageId = 99999; // ID fictif pour test

    private Weapon testWeapon;
    private Potion testPotion;
    private Coffre testCoffre;

    @BeforeEach
    void setup() {
        inventaireDAO = new InventaireDAOImpl("MongoDBTest");
        inventaireDAO.viderInventaire(testPersonnageId);
        inventaireDAO.initialiserInventaireVide(testPersonnageId);

        testWeapon = new Weapon("épée en bois", 30, 20.0, 5);
        testPotion = new Potion("Potion de soin de base", 35, 20.0, 1);
        testCoffre = new Coffre("Coffre", 5.0);
    }

    @BeforeEach
    public void DisplayName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @AfterAll
    void cleanUp() {
        inventaireDAO.viderInventaire(testPersonnageId);
    }

    @Test
    @DisplayName("1: Initialiser et charger un inventaire vide")
    void testInitialiserEtChargerInventaireVide() {
        List<Item> items = inventaireDAO.chargerInventaire(testPersonnageId);
        assertNotNull(items);
        assertTrue(items.isEmpty(), "Inventaire devrait être vide après initialisation.");
    }

    @Test
    @DisplayName("2: Ajouter des items dans l'inventaire avec une seule restriction de coffre")
    void testAjouterItemDansInventaire() {
        inventaireDAO.viderInventaire(testPersonnageId);

        boolean ajoutWeapon = inventaireDAO.ajouterItem(testWeapon, testPersonnageId);
        assertTrue(ajoutWeapon, "On doit pouvoir ajouter une arme.");

        boolean ajoutPotion = inventaireDAO.ajouterItem(testPotion, testPersonnageId);
        assertTrue(ajoutPotion, "On doit pouvoir ajouter une potion.");

        boolean ajoutCoffre1 = inventaireDAO.ajouterItem(testCoffre, testPersonnageId);
        assertTrue(ajoutCoffre1, "On doit pouvoir ajouter un coffre quand il n'y en a pas.");

        Coffre autreCoffre = new Coffre("Second coffre", 100);
        boolean ajoutCoffre2 = inventaireDAO.ajouterItem(autreCoffre, testPersonnageId);
        assertFalse(ajoutCoffre2, "Ajout d'un second coffre doit échouer.");

        List<Item> items = inventaireDAO.chargerInventaire(testPersonnageId);
        assertEquals(3, items.size(), "Inventaire devrait contenir 3 items.");
    }

    @Test
    @DisplayName("3: Vérifier la présence d'un coffre dans l'inventaire")
    void testHasCoffreInInventory() {
        inventaireDAO.ajouterItem(testCoffre, testPersonnageId);
        boolean hasCoffre = inventaireDAO.hasCoffreInInventory(testPersonnageId);
        assertTrue(hasCoffre, "Le coffre doit être détecté dans l'inventaire.");
    }

    @Test
    @DisplayName("4: Récupérer un item par son ID")
    void testRecupererItemParId() {
        inventaireDAO.ajouterItem(testWeapon, testPersonnageId);
        List<Item> items = inventaireDAO.chargerInventaire(testPersonnageId);
        assertFalse(items.isEmpty(), "L'inventaire ne doit pas être vide.");

        Item premierItem = items.get(0);
        Item recuperedItem = inventaireDAO.recupererItemParId(premierItem.getId(), testPersonnageId);

        assertNotNull(recuperedItem, "L'item doit être retrouvé par son ID.");
        assertEquals(premierItem.getId(), recuperedItem.getId(), "IDs doivent correspondre.");
    }

    @Test
    @DisplayName("5: Supprimer un item de l'inventaire")
    void testDeleteItem() {
        inventaireDAO.ajouterItem(testWeapon, testPersonnageId);
        List<Item> items = inventaireDAO.chargerInventaire(testPersonnageId);
        assertFalse(items.isEmpty());

        Item itemToDelete = items.get(0);
        String result = inventaireDAO.DeleteItem(itemToDelete.getId());

        assertTrue(result.contains("supprimé"), "L'item doit être supprimé.");
        Item deletedItem = inventaireDAO.recupererItemParId(itemToDelete.getId(), testPersonnageId);
        assertNull(deletedItem, "L'item supprimé ne doit plus être retrouvé.");
    }

    @Test
    @DisplayName("6: Utiliser une potion et vérifier son effet")
    void testUseItemPotion() {
        inventaireDAO.viderInventaire(testPersonnageId);
        inventaireDAO.ajouterItem(testPotion, testPersonnageId);

        Personnage user = new FistFire("TestUser");
        user.setNom("UtilisateurTest");
        UseItemResult result = inventaireDAO.UseItem(testPotion, user, null, testPersonnageId);

        assertNotNull(result);
        assertTrue(result.message().contains("récupère"), "Le message doit indiquer que la potion a été utilisée.");
        assertEquals(testPotion.getPointsDeVieRecuperes(), result.pointsDeVieRendues());
        assertEquals(0, result.degatsInfliges());
    }

    @Test
    @DisplayName("7: Utiliser une arme pour attaquer une cible")
    void testUseItemWeapon() {
        inventaireDAO.viderInventaire(testPersonnageId);
        inventaireDAO.ajouterItem(testWeapon, testPersonnageId);

        Personnage user = new WaterWa("TestUser");
        user.setNom("Attaquant");
        Personnage cible = new Minotaurus("TestCible", 1);
        cible.setNom("Cible");

        UseItemResult result = inventaireDAO.UseItem(testWeapon, user, cible, testPersonnageId);

        assertNotNull(result);
        assertTrue(result.message().contains("attaque"));
        assertEquals(testWeapon.getDegats(), result.degatsInfliges());
    }

    @Test
    @DisplayName("8: Ajouter un item dans un coffre et le supprimer")
    void testAjouterEtSupprimerItemDansCoffre() {
        inventaireDAO.viderInventaire(testPersonnageId);
        inventaireDAO.ajouterItem(testCoffre, testPersonnageId);
        inventaireDAO.ajouterItem(testWeapon, testPersonnageId);

        boolean ajoute = inventaireDAO.ajouterItemDansCoffre(testWeapon, testPersonnageId);
        assertTrue(ajoute, "L'item doit être ajouté dans le coffre.");

        List<Item> contenu = inventaireDAO.recupererContenuCoffre(testPersonnageId);
        assertEquals(1, contenu.size(), "Le coffre doit contenir 1 item.");

        boolean supprime = inventaireDAO.supprimerItemDuCoffre(testWeapon.getId(), testPersonnageId);
        assertTrue(supprime, "L'item doit être supprimé du coffre.");

        List<Item> contenuApresSuppression = inventaireDAO.recupererContenuCoffre(testPersonnageId);
        assertTrue(contenuApresSuppression.isEmpty(), "Le coffre doit être vide après suppression.");
    }

    @Test
    @DisplayName("9: Vider l'inventaire")
    void testViderInventaire() {
        inventaireDAO.viderInventaire(testPersonnageId);
        List<Item> items = inventaireDAO.chargerInventaire(testPersonnageId);
        assertTrue(items.isEmpty(), "L'inventaire doit être vide après vidage.");
    }
}
