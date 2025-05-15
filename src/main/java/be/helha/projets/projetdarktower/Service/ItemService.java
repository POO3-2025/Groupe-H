package be.helha.projets.projetdarktower.Service;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Model.Personnage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final InventaireDAOImpl inventaireDAO;

    @Autowired
    public ItemService(InventaireDAOImpl inventaireDAO) {
        this.inventaireDAO = inventaireDAO;
    }

    public void viderInventaire(int idPersonnage) {
        inventaireDAO.viderInventaire(idPersonnage);
    }

    public void initialiserInventaire(int idPersonnage) {
        inventaireDAO.initialiserInventaireVide(idPersonnage);
    }

    public boolean ajouterItem(Item item, int idPersonnage) {
        return inventaireDAO.ajouterItem(item, idPersonnage);
    }

    public List<Item> chargerInventaire(int idPersonnage) {
        return inventaireDAO.chargerInventaire(idPersonnage);
    }

    public Item recupererItemParId(String itemId) {
        return inventaireDAO.recupererItemParId(itemId);
    }

    public UseItemResult utiliserItem(Item item, Personnage utilisateur, Personnage cible) {
        return inventaireDAO.UseItem(item, utilisateur, cible);
    }

    public String supprimerItem(String itemId) {
        return inventaireDAO.DeleteItem(itemId);
    }

    public boolean possedeCoffre(String userId) {
        return inventaireDAO.hasCoffreInInventory(userId);
    }

    public List<Item> recupererContenuCoffre(int idPersonnage) {
        return inventaireDAO.recupererContenuCoffre(idPersonnage);
    }

    public boolean ajouterItemDansCoffre(Item item, int idPersonnage) {
        return inventaireDAO.ajouterItemDansCoffre(item, idPersonnage);
    }

    public boolean supprimerItemDuCoffre(String itemId, int idPersonnage) {
        return inventaireDAO.supprimerItemDuCoffre(itemId, idPersonnage);
    }
}
