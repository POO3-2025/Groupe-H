package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
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
    public ResponseEntity<UseItemResult> useItem(@PathVariable String id, @RequestBody @Valid ItemSelectionRequest request) {
        // Récupération du personnage utilisateur
        Personnage utilisateur = characterService.selectCharacter(id);
        if (utilisateur == null) {
            return ResponseEntity.status(404).body(new UseItemResult("Personnage utilisateur non trouvé.", 0, 0, null));
        }

        // Récupération de l'item à utiliser
        Item item = itemService.recupererItemParId(request.getItemId());
        if (item == null) {
            return ResponseEntity.status(404).body(new UseItemResult("Objet non trouvé.", 0, 0, null));
        }

        // Récupération de la cible si spécifiée
        Personnage cible = null;
        if (request.getCibleId() != null) {
            cible = characterService.selectCharacter(request.getCibleId());
            if (cible == null) {
                return ResponseEntity.status(404).body(new UseItemResult("Cible non trouvée.", 0, 0, null));
            }
        }

        // Utilisation de l'item
        UseItemResult resultat = itemService.utiliserItem(item, utilisateur, cible);

        // Retourne le résultat avec les informations mises à jour
        return ResponseEntity.ok(resultat);
    }
}
