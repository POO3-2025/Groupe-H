package be.helha.projets.projetdarktower.Model;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Inventaire.InventaireDAOImpl;
import be.helha.projets.projetdarktower.Item.Item;

public class CombatManager {

    private final InventaireDAOImpl inventaireDAO;

    public CombatManager(InventaireDAOImpl inventaireDAO) {
        this.inventaireDAO = inventaireDAO;
    }

    public UseItemResult utiliserObjet(Item item, Personnage utilisateur, Personnage cible) {
        if (item == null) {
            return new UseItemResult("Aucun objet spécifié.", utilisateur.getPointsDeVie(),
                    cible != null ? cible.getPointsDeVie() : -1, null);
        }

        // Appelle la méthode UseItem de InventaireDAOImpl
        return inventaireDAO.UseItem(item, utilisateur, cible);
    }
}