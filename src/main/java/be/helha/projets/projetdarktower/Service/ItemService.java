package be.helha.projets.projetdarktower.Service;

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

    public boolean ajouterItem(Item item, int idPersonnage) {
        return inventaireDAO.ajouterItem(item, idPersonnage);
    }

    public List<Item> chargerInventaire(int idPersonnage) {
        return inventaireDAO.chargerInventaire(idPersonnage);
    }

    public Item recupererItemParId(String itemId) {
        return inventaireDAO.recupererItemParId(itemId);
    }

    public String utiliserItem(Item item, Personnage utilisateur, Personnage cible) {
        return inventaireDAO.UseItem(item, utilisateur, cible);
    }

    public String utiliserItem(Item item, Personnage utilisateur) {
        return utiliserItem(item, utilisateur, null);
    }
}
