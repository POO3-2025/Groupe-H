package be.helha.projets.projetdarktower.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Fabrique d'items permettant de créer des objets spécifiques (Weapon, Potion, Coffre)
 * à partir d'un nom donné.
 *
 * <p>Fournit aussi une méthode pour récupérer tous les items connus sous forme de map.</p>
 */
public class ItemFactory {

    /**
     * Crée un item concret selon le nom fourni.
     *
     * <p>Les noms reconnus correspondent aux armes en bois, fer, diamant,
     * potions de soin et coffres. Sinon, une exception est levée.</p>
     *
     * @param nom Nom de l'item à créer (insensible à la casse).
     * @return Instance d'Item correspondante.
     * @throws IllegalArgumentException si le nom n'est pas reconnu.
     */
    public static Item creerItem(String nom) {
        Item item;

        switch (nom.toLowerCase()) {
            // Armes en bois (communes)
            case "épée en bois":
                item = new Weapon("Épée en bois", 30, 20.0,5);
                break;
            case "couteau en bois":
                item = new Weapon("Couteau en bois", 25, 15.0,7);
                break;
            case "hache en bois":
                item = new Weapon("Hache en bois", 28, 20.0,6);
                break;

            // Armes en fer (intermédiaires)
            case "épée en fer":
                item = new Weapon("Épée en fer", 50, 4.0,3);
                break;
            case "couteau en fer":
                item = new Weapon("Couteau en fer", 45, 4.0,3);
                break;
            case "hache en fer":
                item = new Weapon("Hache en fer", 55, 4.0,2);
                break;

            // Armes en diamant (rares)
            case "épée en diamant":
                item = new Weapon("Épée en diamant", 90, 1.0,1);
                break;
            case "couteau en diamant":
                item = new Weapon("Couteau en diamant", 80, 1.0,1);
                break;
            case "hache en diamant":
                item = new Weapon("Hache en diamant", 100, 1.0,1);
                break;

            // Potions
            case "potion de soin de base":
                item = new Potion("Potion de soin de base", 35, 20.0,1);
                break;
            case "potion de soin intermédiaire":
                item = new Potion("Potion de soin intermédiaire", 65, 4.0,1);
                break;
            case "total soin":
                item = new Potion("Total soin", 150, 1.0,1);
                break;

            // Coffre
            case "coffre":
                item = new Coffre("Coffre", 5.0);
                break;

            default:
                throw new IllegalArgumentException("Objet inconnu: " + nom);
        }

        // Génère un ID unique et définit le type de l'item
        item.setId(UUID.randomUUID().toString());
        item.setType(item.getClass().getSimpleName());
        return item;
    }

    /**
     * Retourne une map de tous les items connus avec leur nom comme clé.
     *
     * <p>Cette map contient armes, potions et coffre.</p>
     *
     * @return map nom -> item
     */
    public static Map<String, Item> getAllItems() {
        Map<String, Item> map = new HashMap<>();

        // Armes en bois (communes)
        map.put("épée en bois", new Weapon("Épée en bois", 30, 20.0,5));
        map.put("couteau en bois", new Weapon("Couteau en bois", 25, 15.0,7));
        map.put("hache en bois", new Weapon("Hache en bois", 28, 20.0,6));

        // Armes en fer (intermédiaires)
        map.put("épée en fer", new Weapon("Épée en fer", 50, 4.0,3));
        map.put("couteau en fer", new Weapon("Couteau en fer", 45, 4.0,3));
        map.put("hache en fer", new Weapon("Hache en fer", 55, 4.0,2));

        // Armes en diamant (rares)
        map.put("épée en diamant", new Weapon("Épée en diamant", 90, 1.0,1));
        map.put("couteau en diamant", new Weapon("Couteau en diamant", 80, 1.0,1));
        map.put("hache en diamant", new Weapon("Hache en diamant", 100, 1.0,1));

        // Potions
        map.put("potion de soin de base", new Potion("Potion de soin de base", 35, 20.0,1));
        map.put("potion de soin intermédiaire", new Potion("Potion de soin intermédiaire", 65, 4.0,1));
        map.put("total soin", new Potion("Total soin", 150, 1.0,1));

        // Coffre
        map.put("coffre", new Coffre("Coffre", 5.0));

        return map;
    }
}
