package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Item.ItemSelectionRequest;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Service.CharacterService;
import be.helha.projets.projetdarktower.Service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Combat")
public class CombatController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CharacterService characterService;

    @PostMapping("/{id}/use-item")
    public ResponseEntity<String> useItem(@PathVariable String id, @RequestBody ItemSelectionRequest request) {
        // Récupère le personnage utilisateur
        Personnage utilisateur = characterService.selectCharacter(id);
        if (utilisateur == null) {
            return ResponseEntity.status(404).body("Personnage non trouvé.");
        }

        // Récupère l’item sélectionné
        Item item = itemService.recupererItemParId(request.getItemId());
        if (item == null) {
            return ResponseEntity.status(404).body("Objet non trouvé.");
        }

        // Récupère la cible si un ID est fourni
        Personnage cible = null;
        if (request.getCibleId() != null) {
            cible = characterService.selectCharacter(request.getCibleId());
            if (cible == null) {
                return ResponseEntity.status(404).body("Cible non trouvée.");
            }
        }

        // Utilise l'item avec ou sans cible
        String resultat = itemService.utiliserItem(item, utilisateur, cible);
        return ResponseEntity.ok(resultat);
    }
}
