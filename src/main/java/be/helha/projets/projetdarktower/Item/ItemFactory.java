package be.helha.projets.projetdarktower.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemFactory {

    public static Item creerItem(String nom) {
        Item item;

        switch (nom.toLowerCase()) {
            // Armes en bois (communes)
            case "épée en bois":
                item = new Weapon("Épée en bois", 20, 15.0);
                break;
            case "couteau en bois":
                item = new Weapon("Couteau en bois", 15, 15.0);
                break;
            case "hache en bois":
                item = new Weapon("Hache en bois", 18, 15.0);
                break;

            // Armes en fer (intermédiaires)
            case "épée en fer":
                item = new Weapon("Épée en fer", 40, 8.0);
                break;
            case "couteau en fer":
                item = new Weapon("Couteau en fer", 35, 8.0);
                break;
            case "hache en fer":
                item = new Weapon("Hache en fer", 45, 8.0);
                break;

            // Armes en diamant (rares)
            case "épée en diamant":
                item = new Weapon("Épée en diamant", 70, 3.0);
                break;
            case "couteau en diamant":
                item = new Weapon("Couteau en diamant", 60, 3.0);
                break;
            case "hache en diamant":
                item = new Weapon("Hache en diamant", 80, 3.0);
                break;

            // Potions
            case "potion de soin de base":
                item = new Potion("Potion de soin de base", 25, 11.0);
                break;
            case "potion de soin intermédiaire":
                item = new Potion("Potion de soin intermédiaire", 50, 9.0);
                break;
            case "total soin":
                item = new Potion("Total soin", 100, 2.0);
                break;

            // Autres
            case "coffre":
                item = new Coffre("Coffre", 4.0);
                break;

            default:
                throw new IllegalArgumentException("Objet inconnu: " + nom);
        }

        item.setId(UUID.randomUUID().toString());
        item.setType(item.getClass().getSimpleName());
        return item;
    }

    public static Map<String, Item> getAllItems() {
        Map<String, Item> map = new HashMap<>();

        // Armes en bois (communes)
        map.put("épée en bois", new Weapon("Épée en bois", 20, 15.0));
        map.put("couteau en bois", new Weapon("Couteau en bois", 15, 15.0));
        map.put("hache en bois", new Weapon("Hache en bois", 18, 15.0));

        // Armes en fer (intermédiaires)
        map.put("épée en fer", new Weapon("Épée en fer", 40, 8.0));
        map.put("couteau en fer", new Weapon("Couteau en fer", 35, 8.0));
        map.put("hache en fer", new Weapon("Hache en fer", 45, 8.0));

        // Armes en diamant (rares)
        map.put("épée en diamant", new Weapon("Épée en diamant", 70, 3.0));
        map.put("couteau en diamant", new Weapon("Couteau en diamant", 60, 3.0));
        map.put("hache en diamant", new Weapon("Hache en diamant", 80, 3.0));

        // Potions
        map.put("potion de soin de base", new Potion("Potion de soin de base", 25, 11.0));
        map.put("potion de soin intermédiaire", new Potion("Potion de soin intermédiaire", 50, 9.0));
        map.put("total soin", new Potion("Total soin", 100, 2.0));

        // Coffre
        map.put("coffre", new Coffre("Coffre", 4.0));

        return map;
    }
}
