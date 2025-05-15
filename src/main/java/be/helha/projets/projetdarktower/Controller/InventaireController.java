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
}