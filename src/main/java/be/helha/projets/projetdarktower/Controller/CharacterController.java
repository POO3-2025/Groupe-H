package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.*;
import be.helha.projets.projetdarktower.Service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des personnages.
 * Permet notamment la sélection d'un personnage par un utilisateur.
 */
@RestController
@RequestMapping("/characters")
public class CharacterController {

    @Autowired
    private CharacterService characterService;

    /**
     * Map stockant l'association entre un ID utilisateur et son personnage sélectionné.
     */
    private final Map<String, Personnage> joueurs = new HashMap<>();

    /**
     * Endpoint pour sélectionner un personnage par l'utilisateur.
     *
     * @param request Requête contenant l'ID du personnage et de l'utilisateur.
     * @return ResponseEntity avec un message de succès ou d'erreur (404 si personnage non trouvé).
     */
    @PostMapping("/select")
    public ResponseEntity<String> selectCharacter(@RequestBody CharacterSelectionRequest request) {
        Personnage personnage = characterService.selectCharacter(request.getCharacterId());
        if (personnage != null) {
            joueurs.put(request.getUserId(), personnage); // Associe l'utilisateur à un personnage
            return ResponseEntity.ok("Personnage " + personnage.getNom() + " sélectionné !");
        } else {
            return ResponseEntity.status(404).body("Personnage non trouvé.");
        }
    }
}
