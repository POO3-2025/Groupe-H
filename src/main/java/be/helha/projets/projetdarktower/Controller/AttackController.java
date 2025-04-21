package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Service.AttackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attack")
public class AttackController {

    @Autowired
    private AttackService attackService;

    @PostMapping("/{attackerId}/{targetId}")
    public String attack(@PathVariable String attackerId, @PathVariable String targetId, @RequestParam String attackType) {
        attackService.processAttack(attackerId, targetId, attackType);
        return "Attaque réussie!";
    }
}
