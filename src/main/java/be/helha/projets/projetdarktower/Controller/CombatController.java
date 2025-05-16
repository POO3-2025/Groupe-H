package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Item.ItemSelectionRequest;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Model.User;
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

    @PostMapping("/{idUser}/{idPersonnage}/use-item")
    public ResponseEntity<UseItemResult> useItem(@PathVariable String idPersonnage,
                                                 @PathVariable int idUser,
                                                 @RequestBody @Valid ItemSelectionRequest request) {
        System.out.println("ID personnage utilisateur reçu : " + idPersonnage);
        System.out.println("ID propriétaire inventaire reçu : " + idUser);

        Personnage utilisateur = characterService.selectCharacter(idPersonnage);
        if (utilisateur == null) {
            return ResponseEntity.status(404).body(new UseItemResult("Personnage utilisateur non trouvé.", 0, 0, null));
        }

        Item item = itemService.recupererItemParId(request.getItemId(), idUser);
        if (item == null) {
            return ResponseEntity.status(404).body(new UseItemResult("Objet non trouvé.", 0, 0, null));
        }

        Personnage cible = null;
        if (request.getCibleId() != null) {
            cible = characterService.selectCharacter(request.getCibleId());
            if (cible == null) {
                return ResponseEntity.status(404).body(new UseItemResult("Cible non trouvée.", 0, 0, null));
            }
        }

        UseItemResult resultat = itemService.utiliserItem(item, utilisateur, cible ,idUser);
        System.out.println("Résultat de l'utilisation de l'item : " + resultat);
        return ResponseEntity.ok(resultat);
    }

}
