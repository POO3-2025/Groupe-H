package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Model.Personnage;

import java.util.List;

public interface InventaireDAO {

    boolean hasCoffreInInventory(int idPersonnage);

    boolean ajouterItem(Item item, int idPersonnage);

    List<Item> chargerInventaire(int idPersonnage);

    Item recupererItemParId(String itemId, int idPersonnage);

    String DeleteItem(String itemId);

    UseItemResult UseItem(Item item, Personnage utilisateur, Personnage cible, int UserId);

    void initialiserInventaireVide(int idPersonnage);

    void viderInventaire(int idPersonnage);

    List<Item> recupererContenuCoffre(int idPersonnage);

    boolean ajouterItemDansCoffre(Item item, int idPersonnage);

    boolean supprimerItemDuCoffre(String itemId, int idPersonnage);
}
