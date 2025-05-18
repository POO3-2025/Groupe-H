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

/**
 * Contrôleur REST dédié à la gestion des combats.
 * Permet l'utilisation d'items par un personnage sur une cible éventuelle.
 */
@RestController
@RequestMapping("/Combat")
public class CombatController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CharacterService characterService;

    /**
     * Endpoint pour utiliser un item dans le cadre d'un combat.
     *
     * @param idPersonnage Identifiant du personnage utilisateur (celui qui utilise l'item).
     * @param idUser       Identifiant du propriétaire de l'inventaire contenant l'item.
     * @param request      Requête contenant l'ID de l'item à utiliser et éventuellement l'ID de la cible.
     * @return ResponseEntity contenant le résultat de l'utilisation de l'item ou un message d'erreur.
     */
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

        UseItemResult resultat = itemService.utiliserItem(item, utilisateur, cible, idUser);
        System.out.println("Résultat de l'utilisation de l'item : " + resultat);
        return ResponseEntity.ok(resultat);
    }
}
