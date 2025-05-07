package be.helha.projets.projetdarktower.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemFactory {

    public static Item creerItem(String nom) {
        Item item;

        switch (nom.toLowerCase()) {
            // Armes en bois (faibles)
            case "épée en bois":
                item = new Weapon("Épée en bois", 20, 30.0);
                break;
            case "couteau en bois":
                item = new Weapon("Couteau en bois", 15, 25.0);
                break;
            case "hache en bois":
                item = new Weapon("Hache en bois", 18, 20.0);
                break;

            // Armes en fer (moyennes)
            case "épée en fer":
                item = new Weapon("Épée en fer", 40, 15.0);
                break;
            case "couteau en fer":
                item = new Weapon("Couteau en fer", 35, 12.0);
                break;
            case "hache en fer":
                item = new Weapon("Hache en fer", 45, 10.0);
                break;

            // Armes en diamant (fortes)
            case "épée en diamant":
                item = new Weapon("Épée en diamant", 70, 5.0);
                break;
            case "couteau en diamant":
                item = new Weapon("Couteau en diamant", 60, 4.0);
                break;
            case "hache en diamant":
                item = new Weapon("Hache en diamant", 80, 3.0);
                break;

            // Potions
            case "potion de soin de base":
                item = new Potion("Potion de soin de base", 25, 40.0);
                break;
            case "potion de soin intermédiaire":
                item = new Potion("Potion de soin intermédiaire", 50, 30.0);
                break;
            case "total soin":
                item = new Potion("Total soin", 100, 10.0);
                break;

            // Autres
            case "coffre":
                item = new Coffre("Coffre", 8.0);
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

        map.put("épée en bois", new Weapon("Épée en bois", 50, 30.0));
        map.put("couteau en bois", new Weapon("Couteau en bois", 30, 25.0));
        map.put("hache en bois", new Weapon("Hache en bois", 40, 20.0));

        map.put("épée en fer", new Weapon("Épée en fer", 70, 15.0));
        map.put("couteau en fer", new Weapon("Couteau en fer", 60, 12.0));
        map.put("hache en fer", new Weapon("Hache en fer", 80, 10.0));

        map.put("épée en diamant", new Weapon("Épée en diamant", 150, 5.0));
        map.put("couteau en diamant", new Weapon("Couteau en diamant", 120, 4.0));
        map.put("hache en diamant", new Weapon("Hache en diamant", 200, 3.0));

        map.put("potion de soin de base", new Potion("Potion de soin de base", 25, 40.0));
        map.put("potion de soin intermédiaire", new Potion("Potion de soin intermédiaire", 50, 30.0));
        map.put("total soin", new Potion("Total soin", 100, 10.0));

        map.put("coffre", new Coffre("Coffre", 8.0));

        return map;
    }
}
