package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.*;
import be.helha.projets.projetdarktower.Service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/characters")
public class CharacterController {

    @Autowired
    private CharacterService characterService;

    @PostMapping("/select")
    public ResponseEntity<String> selectCharacter(@RequestBody CharacterSelectionRequest request) {
        Personnage personnage = characterService.selectCharacter(request.getCharacterId());
        if (personnage != null) {
            return ResponseEntity.ok("Personnage " + personnage.getNom() + " sélectionné !");
        } else {
            return ResponseEntity.status(404).body("Personnage non trouvé.");
        }
    }






}
