package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.Item.Item;
import java.util.List;

public interface InventaireDAO {

    boolean ajouterItem(Item item, int idPersonnage);

    List<Item> chargerInventaire(int idPersonnage);

    Item recupererItemParId(String itemId);

    boolean hasCoffreInInventory(String userId);

    public void initialiserInventaireVide(int idPersonnage);

}
