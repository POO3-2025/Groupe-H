package be.helha.projets.projetdarktower.Service;

import be.helha.projets.projetdarktower.Model.*;
import be.helha.projets.projetdarktower.LanternaCombat;  // Importer LanternaCombat pour accéder à Etage

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CharacterService {

    private Map<String, Personnage> personnages = new HashMap<>();

    public CharacterService() {
        // Ajoute les personnages
        personnages.put("fistfire", new FistFire("1"));
        personnages.put("waterwa", new WaterWa("2"));
        personnages.put("jowind", new JoWind("3"));
        personnages.put("twood", new TWood("4"));
        personnages.put("Minotaurus", new Minotaurus("999",1)); // Personnage de test

          // On passe le niveau récupéré
    }

    public Personnage selectCharacter(String characterId) {
            System.out.println("Recherche du personnage avec l'ID : " + characterId);
        if ("999".equals(characterId)) {
            return personnages.get("Minotaurus");
        }
        if ("1".equals(characterId) ) {
            return personnages.get("fistfire");
        }
        if ("2".equals(characterId) ) {
            return personnages.get("waterwa");
        }
        if ("3".equals(characterId) ) {
            return personnages.get("jowind");
        }
        if ("4".equals(characterId) ) {
            return personnages.get("twood");
        }
        return personnages.get(characterId);
    }

}
