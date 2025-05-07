package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.Item.Item;
import java.util.List;

public interface InventaireDAO {

    boolean ajouterItem(Item item);

    List<Item> chargerInventaire();

    Item recupererItemParId(String itemId);

}
