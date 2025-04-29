package be.helha.projets.projetdarktower.Service;

import be.helha.projets.projetdarktower.Model.*;

import org.springframework.stereotype.Service; // Importer cette annotation

import java.util.HashMap;
import java.util.Map;

@Service // Ajoute cette annotation pour définir CharacterService comme un bean Spring
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
}
