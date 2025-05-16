package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Model.Personnage;

import java.util.List;

public interface InventaireDAO {

    boolean ajouterItem(Item item, int idPersonnage);

    List<Item> chargerInventaire(int idPersonnage);

    Item recupererItemParId(String itemId,int idPersonnage);

    boolean hasCoffreInInventory(int userId);

    void initialiserInventaireVide(int idPersonnage);

    void viderInventaire(int idPersonnage);

    String DeleteItem(String itemId);

    UseItemResult UseItem(Item item, Personnage utilisateur, Personnage cible,int UserId);
}
