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

    public boolean ajouterItem(Item item) {
        return inventaireDAO.ajouterItem(item);
    }

    public List<Item> chargerInventaire() {
        return inventaireDAO.chargerInventaire();
    }

    public Item recupererItemParId(String itemId) {
        return inventaireDAO.recupererItemParId(itemId);
    }

    /**
     * Utilise un item (arme, potion, etc.)
     * @param item      L'objet à utiliser.
     * @param utilisateur Le personnage qui utilise l'objet.
     * @param cible     Le personnage ciblé (null si aucun).
     * @return Résultat de l'utilisation.
     */
    public String utiliserItem(Item item, Personnage utilisateur, Personnage cible) {
        return inventaireDAO.UseItem(item, utilisateur, cible);
    }

    /**
     * Variante : utilise un objet sans cible.
     */
    public String utiliserItem(Item item, Personnage utilisateur) {
        return utiliserItem(item, utilisateur, null);
    }
}
