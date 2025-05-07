package be.helha.projets.projetdarktower.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemFactory {

    public static Item creerItem(String nom) {
        Item item;

        switch (nom.toLowerCase()) {
            case "épée en bois":
                item = new Epee("Épée en bois", 50);
                break;
            case "couteau en bois":
                item = new Epee("Couteau en bois", 30);
                break;
            case "hache en bois":
                item = new Epee("Hache en bois", 40);
                break;

            case "épée en fer":
                item = new Epee("Épée en fer", 70);
                break;
            case "couteau en fer":
                item = new Epee("Couteau en fer", 60);
                break;
            case "hache en fer":
                item = new Epee("Hache en fer", 80);
                break;

            case "épée en diamant":
                item = new Epee("Épée en diamant", 150);
                break;
            case "couteau en diamant":
                item = new Epee("Couteau en diamant", 120);
                break;
            case "hache en diamant":
                item = new Epee("Hache en diamant", 200);
                break;

            case "potion de soin":
                item = new Potion("Potion de soin", 100);
                break;
            case "potion de mana":
                item = new Potion("Potion de mana", 50);
                break;

            case "coffre":
                item = new Coffre("Coffre");
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

        map.put("épée en bois", new Epee("Épée en bois", 50));
        map.put("couteau en bois", new Epee("Couteau en bois", 30));
        map.put("hache en bois", new Epee("Hache en bois", 40));

        map.put("épée en fer", new Epee("Épée en fer", 70));
        map.put("couteau en fer", new Epee("Couteau en fer", 60));
        map.put("hache en fer", new Epee("Hache en fer", 80));

        map.put("épée en diamant", new Epee("Épée en diamant", 150));
        map.put("couteau en diamant", new Epee("Couteau en diamant", 120));
        map.put("hache en diamant", new Epee("Hache en diamant", 200));

        map.put("potion de soin", new Potion("Potion de soin", 100));
        map.put("potion de mana", new Potion("Potion de mana", 50));

        map.put("coffre", new Coffre("Coffre"));

        return map;
    }
}
