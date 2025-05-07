package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Inventaire.InventaireDAO;
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
    private InventaireDAO inventaireDAO;

    @Autowired
    private CharacterService characterService;

    @PostMapping("/{id}/use-item")
    public ResponseEntity<String> useItem(@PathVariable String id, @RequestBody ItemSelectionRequest request) {
        Personnage personnage = characterService.selectCharacter(id);
        if (personnage == null) {
            return ResponseEntity.status(404).body("Personnage non trouvé.");
        }

        Item item = InventaireDAO.recupererItemParId(request.getItemId());
        if (item == null) {
            return ResponseEntity.status(404).body("Objet non trouvé.");
        }

        String resultat = item.utiliser(personnage); // Supposé que ton Item a une méthode `utiliser(Personnage)`
        return ResponseEntity.ok(resultat);
    }
}
