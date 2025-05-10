package be.helha.projets.projetdarktower.Model;

import be.helha.projets.projetdarktower.Model.*;

public class PersonnageFactory {

    public static Personnage createPersonnage(String characterId, String userId) {
        switch (characterId.toLowerCase()) {
            case "fistfire":
                return new FistFire(userId);
            case "jowind":
                return new JoWind(userId);
            case "twood":
                return new TWood(userId);
            case "waterwa":
                return new WaterWa(userId);
            default:
                throw new IllegalArgumentException("Type de personnage inconnu : " + characterId);
        }
    }
}

