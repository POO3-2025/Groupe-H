package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.LanternaCombat;
import be.helha.projets.projetdarktower.Model.Minotaurus;
import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Item.ItemSelectionRequest;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Service.CharacterService;
import be.helha.projets.projetdarktower.Service.ItemService;
import jakarta.validation.Valid;
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
    public ResponseEntity<String> useItem(@PathVariable String id, @RequestBody @Valid ItemSelectionRequest request) {
        Personnage utilisateur = characterService.selectCharacter(id);
        if (utilisateur == null) {
            return ResponseEntity.status(404).body("Personnage utilisateur non trouvé.");
        }

        Item item = itemService.recupererItemParId(request.getItemId());
        if (item == null) {
            return ResponseEntity.status(404).body("Objet non trouvé.");
        }

        Personnage cible = null;
        if (request.getCibleId() != null) {
                cible = characterService.selectCharacter(request.getCibleId());

            if (cible == null) {
                return ResponseEntity.status(404).body("Cible non trouvée.");
            }
        }

        String resultat = itemService.utiliserItem(item, utilisateur, cible);
        return ResponseEntity.ok(resultat);
    }


}
