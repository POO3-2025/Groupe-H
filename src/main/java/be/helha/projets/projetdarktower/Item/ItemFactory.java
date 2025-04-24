package be.helha.projets.projetdarktower.Item;

public class ItemFactory {


    public static Item creerItem(String type, String nom) {
        switch (type.toLowerCase()) {
            case "potion":
                return new Potion(nom);
            case "epee":
                return new Epee(nom);
            case "coffre":
                return new Coffre(nom);
            default:
                return new Item(nom);
        }
    }
}
