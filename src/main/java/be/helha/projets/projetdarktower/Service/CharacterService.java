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

        // Ajoute le Minotaurus avec le niveau actuel
        int niveauActuel = LanternaCombat.etageActuel.getEtage();
        personnages.put("minotaurus", new Minotaurus("999", niveauActuel));  // On passe le niveau récupéré
    }

    public Personnage selectCharacter(String characterId) {
        return personnages.get(characterId);
    }
}
