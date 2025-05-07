package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import be.helha.projets.projetdarktower.Item.ItemSelectionRequest;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Combat")
public class CombatController {

    @Autowired
    private InventaireDAOImpl inventaireDAO;  // Injection correcte d'InventaireDAOImpl

    @Autowired
    private CharacterService characterService;

    @PostMapping("/{id}/use-item")
    public ResponseEntity<String> useItem(@PathVariable String id, @RequestBody ItemSelectionRequest request) {
        Personnage personnage = characterService.selectCharacter(id);
        if (personnage == null) {
            return ResponseEntity.status(404).body("Personnage non trouvé.");
        }

        Item item = inventaireDAO.recupererItemParId(request.getItemId());  // Utilisation d'une instance d'InventaireDAOImpl
        if (item == null) {
            return ResponseEntity.status(404).body("Objet non trouvé.");
        }

        String resultat = item.UseItem(item,personnage);  // Utilisation de l'item avec la méthode `utiliser` (assurez-vous qu'elle existe)
        return ResponseEntity.ok(resultat);
    }
}
