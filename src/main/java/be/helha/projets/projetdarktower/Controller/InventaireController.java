package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventaire")
public class InventaireController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/{idPersonnage}/vider")
    public ResponseEntity<String> viderInventaire(@PathVariable int idPersonnage) {
        itemService.viderInventaire(idPersonnage);
        return ResponseEntity.ok("Inventaire vidé pour le personnage " + idPersonnage);
    }

    @PostMapping("/{idPersonnage}/initialiser")
    public ResponseEntity<String> initialiserInventaire(@PathVariable int idPersonnage) {
        itemService.initialiserInventaire(idPersonnage);
        return ResponseEntity.ok("Inventaire initialisé pour le personnage " + idPersonnage);
    }

    @PostMapping("/{idPersonnage}/ajouter")
    public ResponseEntity<String> ajouterItem(@PathVariable int idPersonnage, @RequestBody Item item) {
        boolean success = itemService.ajouterItem(item, idPersonnage);
        if (success) {
            return ResponseEntity.ok("Item ajouté avec succès.");
        } else {
            return ResponseEntity.badRequest().body("Impossible d'ajouter l'item (inventaire plein ou autre problème).");
        }
    }

    @GetMapping("/{idPersonnage}/charger")
    public ResponseEntity<List<Item>> chargerInventaire(@PathVariable int idPersonnage) {
        List<Item> inventaire = itemService.chargerInventaire(idPersonnage);
        return ResponseEntity.ok(inventaire);
    }

    @GetMapping("/{idPersonnage}/coffre")
    public ResponseEntity<List<Item>> recupererContenuCoffre(@PathVariable int idPersonnage) {
        List<Item> coffre = itemService.recupererContenuCoffre(idPersonnage);
        return ResponseEntity.ok(coffre);
    }

    @PostMapping("/{idPersonnage}/coffre/ajouter")
    public ResponseEntity<Boolean> ajouterItemDansCoffre(@PathVariable int idPersonnage, @RequestBody Item item) {
        boolean success = itemService.ajouterItemDansCoffre(item, idPersonnage);
        if (success) {
            return ResponseEntity.ok(success);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @DeleteMapping("/{idPersonnage}/coffre/{itemId}")
    public ResponseEntity<String> supprimerItemDuCoffre(@PathVariable int idPersonnage, @PathVariable String itemId) {
        boolean success = itemService.supprimerItemDuCoffre(itemId, idPersonnage);
        if (success) {
            return ResponseEntity.ok("Item supprimé du coffre.");
        } else {
            return ResponseEntity.badRequest().body("Item introuvable dans le coffre.");
        }
    }

    @GetMapping("/coffre/existe/{userId}")
    public ResponseEntity<Boolean> possedeCoffre(@PathVariable int userId) {
        boolean existe = itemService.possedeCoffre(userId);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<Item> recupererItemParId(@PathVariable String itemId ,@PathVariable int idPersonnage) {
        Item item = itemService.recupererItemParId(itemId, idPersonnage);
        if (item != null) {
            return ResponseEntity.ok(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> supprimerItem(@PathVariable String itemId) {
        String result = itemService.supprimerItem(itemId);
        return ResponseEntity.ok(result);
    }
}
