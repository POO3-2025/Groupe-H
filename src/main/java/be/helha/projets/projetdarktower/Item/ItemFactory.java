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
                item = new Weapon("Épée en bois", 20, 20.0,5);
                break;
            case "couteau en bois":
                item = new Weapon("Couteau en bois", 15, 15.0,7);
                break;
            case "hache en bois":
                item = new Weapon("Hache en bois", 18, 20.0,6);
                break;

            // Armes en fer (intermédiaires)
            case "épée en fer":
                item = new Weapon("Épée en fer", 40, 4.0,3);
                break;
            case "couteau en fer":
                item = new Weapon("Couteau en fer", 35, 4.0,3);
                break;
            case "hache en fer":
                item = new Weapon("Hache en fer", 45, 4.0,3);
                break;

            // Armes en diamant (rares)
            case "épée en diamant":
                item = new Weapon("Épée en diamant", 70, 1.0,1);
                break;
            case "couteau en diamant":
                item = new Weapon("Couteau en diamant", 60, 1.0,1);
                break;
            case "hache en diamant":
                item = new Weapon("Hache en diamant", 80, 1.0,1);
                break;

            // Potions
            case "potion de soin de base":
                item = new Potion("Potion de soin de base", 25, 20.0,1);
                break;
            case "potion de soin intermédiaire":
                item = new Potion("Potion de soin intermédiaire", 50, 4.0,1);
                break;
            case "total soin":
                item = new Potion("Total soin", 100, 1.0,1);
                break;

            // Autres
            case "coffre":
                item = new Coffre("Coffre", 5.0);
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
        map.put("épée en bois", new Weapon("Épée en bois", 20, 20.0,5));
        map.put("couteau en bois", new Weapon("Couteau en bois", 15, 15.0,5));
        map.put("hache en bois", new Weapon("Hache en bois", 18, 20.0,5));

        // Armes en fer (intermédiaires)
        map.put("épée en fer", new Weapon("Épée en fer", 40, 4.0,3));
        map.put("couteau en fer", new Weapon("Couteau en fer", 35, 4.0,3));
        map.put("hache en fer", new Weapon("Hache en fer", 45, 4.0,3));

        // Armes en diamant (rares)
        map.put("épée en diamant", new Weapon("Épée en diamant", 70, 1.0,1));
        map.put("couteau en diamant", new Weapon("Couteau en diamant", 60, 1.0,1));
        map.put("hache en diamant", new Weapon("Hache en diamant", 80, 1.0,1));

        // Potions
        map.put("potion de soin de base", new Potion("Potion de soin de base", 25, 20.0,1));
        map.put("potion de soin intermédiaire", new Potion("Potion de soin intermédiaire", 50, 4.0,1));
        map.put("total soin", new Potion("Total soin", 100, 1.0,1));

        // Coffre
        map.put("coffre", new Coffre("Coffre", 50.0));

        return map;
    }
}
