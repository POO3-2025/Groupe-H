package be.helha.projets.projetdarktower.Service;

import be.helha.projets.projetdarktower.Model.*;

import java.util.HashMap;
import java.util.Map;

public class CharacterService {

    private Map<String, Personnage> personnages = new HashMap<>();

    public CharacterService() {
        personnages.put("fistfire", new FistFire("1"));
        personnages.put("waterwa", new WaterWa("2"));
        personnages.put("jowind", new JoWind("3"));
        personnages.put("twood", new TWood("4"));
    }

    public Personnage selectCharacter(String characterId) {
        return personnages.get(characterId);
    }

    public void attack(CharacterService characterService, AttackRequest attackRequest) {
        Personnage attacker = personnages.get(attackRequest.getId());
        Personnage target = personnages.get(attackRequest.getTargetId());

        if (attacker != null && target != null) {
            attacker.attaquer(target, attackRequest.getAttackType());
        }
    }
}
