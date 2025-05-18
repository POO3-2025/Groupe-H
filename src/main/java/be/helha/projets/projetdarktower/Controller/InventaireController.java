package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des inventaires des personnages.
 * Permet de vider, initialiser, ajouter des items, charger l'inventaire, gérer le coffre, etc.
 */
@RestController
@RequestMapping("/inventaire")
public class InventaireController {

    @Autowired
    private ItemService itemService;

    /**
     * Vide l'inventaire d'un personnage donné.
     *
     * @param idPersonnage ID du personnage dont l'inventaire doit être vidé.
     * @return Message confirmant la vidange de l'inventaire.
     */
    @PostMapping("/{idPersonnage}/vider")
    public ResponseEntity<String> viderInventaire(@PathVariable int idPersonnage) {
        itemService.viderInventaire(idPersonnage);
        return ResponseEntity.ok("Inventaire vidé pour le personnage " + idPersonnage);
    }

    /**
     * Initialise un inventaire vide pour un personnage.
     *
     * @param idPersonnage ID du personnage dont l'inventaire doit être initialisé.
     * @return Message confirmant l'initialisation.
     */
    @PostMapping("/{idPersonnage}/initialiser")
    public ResponseEntity<String> initialiserInventaire(@PathVariable int idPersonnage) {
        itemService.initialiserInventaire(idPersonnage);
        return ResponseEntity.ok("Inventaire initialisé pour le personnage " + idPersonnage);
    }

    /**
     * Ajoute un item à l'inventaire d'un personnage.
     *
     * @param idPersonnage ID du personnage concerné.
     * @param item         Item à ajouter.
     * @return Succès ou message d'erreur en cas d'échec (inventaire plein, etc).
     */
    @PostMapping("/{idPersonnage}/ajouter")
    public ResponseEntity<String> ajouterItem(@PathVariable int idPersonnage, @RequestBody Item item) {
        boolean success = itemService.ajouterItem(item, idPersonnage);
        if (success) {
            return ResponseEntity.ok("Item ajouté avec succès.");
        } else {
            return ResponseEntity.badRequest().body("Impossible d'ajouter l'item (inventaire plein ou autre problème).");
        }
    }

    /**
     * Charge l'inventaire complet d'un personnage.
     *
     * @param idPersonnage ID du personnage.
     * @return Liste des items présents dans l'inventaire.
     */
    @GetMapping("/{idPersonnage}/charger")
    public ResponseEntity<List<Item>> chargerInventaire(@PathVariable int idPersonnage) {
        List<Item> inventaire = itemService.chargerInventaire(idPersonnage);
        return ResponseEntity.ok(inventaire);
    }

    /**
     * Récupère la liste des items contenus dans le coffre d'un personnage.
     *
     * @param idPersonnage ID du personnage.
     * @return Liste des items présents dans le coffre.
     */
    @GetMapping("/{idPersonnage}/coffre")
    public ResponseEntity<List<Item>> recupererContenuCoffre(@PathVariable int idPersonnage) {
        List<Item> coffre = itemService.recupererContenuCoffre(idPersonnage);
        return ResponseEntity.ok(coffre);
    }

    /**
     * Ajoute un item dans le coffre d'un personnage.
     *
     * @param idPersonnage ID du personnage.
     * @param item         Item à ajouter dans le coffre.
     * @return True si succès, false sinon.
     */
    @PostMapping("/{idPersonnage}/coffre/ajouter")
    public ResponseEntity<Boolean> ajouterItemDansCoffre(@PathVariable int idPersonnage, @RequestBody Item item) {
        boolean success = itemService.ajouterItemDansCoffre(item, idPersonnage);
        if (success) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    /**
     * Supprime un item du coffre d'un personnage.
     *
     * @param idPersonnage ID du personnage.
     * @param itemId       ID de l'item à supprimer.
     * @return Message indiquant succès ou échec.
     */
    @DeleteMapping("/{idPersonnage}/coffre/{itemId}")
    public ResponseEntity<String> supprimerItemDuCoffre(@PathVariable int idPersonnage, @PathVariable String itemId) {
        boolean success = itemService.supprimerItemDuCoffre(itemId, idPersonnage);
        if (success) {
            return ResponseEntity.ok("Item supprimé du coffre.");
        } else {
            return ResponseEntity.badRequest().body("Item introuvable dans le coffre.");
        }
    }

    /**
     * Vérifie si un utilisateur possède un coffre dans son inventaire.
     *
     * @param userId ID de l'utilisateur.
     * @return True si un coffre existe, false sinon.
     */
    @GetMapping("/coffre/existe/{userId}")
    public ResponseEntity<Boolean> possedeCoffre(@PathVariable int userId) {
        boolean existe = itemService.possedeCoffre(userId);
        return ResponseEntity.ok(existe);
    }

    /**
     * Récupère un item spécifique par son ID dans l'inventaire d'un personnage.
     *
     * @param itemId       ID de l'item.
     * @param idPersonnage ID du personnage.
     * @return L'item si trouvé, sinon réponse 404.
     */
    @GetMapping("/item/{itemId}/{idPersonnage}")
    public ResponseEntity<Item> recupererItemParId(@PathVariable String itemId, @PathVariable int idPersonnage) {
        Item item = itemService.recupererItemParId(itemId, idPersonnage);
        if (item != null) {
            return ResponseEntity.ok(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime un item de l'inventaire par son ID.
     *
     * @param itemId ID de l'item à supprimer.
     * @return Message résultat de la suppression.
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> supprimerItem(@PathVariable String itemId) {
        String result = itemService.supprimerItem(itemId);
        return ResponseEntity.ok(result);
    }
}
