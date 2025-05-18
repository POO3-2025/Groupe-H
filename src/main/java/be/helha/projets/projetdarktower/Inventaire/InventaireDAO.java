package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Model.Personnage;

import java.util.List;

/**
 * Interface DAO pour gérer l'inventaire des personnages.
 * Définit les opérations possibles sur l'inventaire et le coffre.
 */
public interface InventaireDAO {

    /**
     * Vérifie si un coffre est présent dans l'inventaire d'un personnage.
     *
     * @param idPersonnage ID du personnage.
     * @return true si un coffre est présent, false sinon.
     */
    boolean hasCoffreInInventory(int idPersonnage);

    /**
     * Ajoute un item à l'inventaire d'un personnage.
     *
     * @param item         Item à ajouter.
     * @param idPersonnage ID du personnage.
     * @return true si l'ajout a réussi, false sinon (ex : inventaire plein).
     */
    boolean ajouterItem(Item item, int idPersonnage);

    /**
     * Charge la liste des items présents dans l'inventaire d'un personnage.
     *
     * @param idPersonnage ID du personnage.
     * @return Liste des items dans l'inventaire.
     */
    List<Item> chargerInventaire(int idPersonnage);

    /**
     * Récupère un item précis dans l'inventaire par son ID.
     *
     * @param itemId       ID de l'item.
     * @param idPersonnage ID du personnage.
     * @return Item correspondant ou null si introuvable.
     */
    Item recupererItemParId(String itemId, int idPersonnage);

    /**
     * Supprime un item de l'inventaire par son ID.
     *
     * @param itemId ID de l'item à supprimer.
     * @return Message indiquant le résultat de la suppression.
     */
    String DeleteItem(String itemId);

    /**
     * Utilise un item dans le cadre d'un combat ou d'une action.
     *
     * @param item       Item à utiliser.
     * @param utilisateur Personnage qui utilise l'item.
     * @param cible      Personnage cible de l'item (peut être null).
     * @param UserId     ID du propriétaire de l'inventaire.
     * @return Résultat détaillé de l'utilisation de l'item.
     */
    UseItemResult UseItem(Item item, Personnage utilisateur, Personnage cible, int UserId);

    /**
     * Initialise un inventaire vide pour un personnage donné.
     *
     * @param idPersonnage ID du personnage.
     */
    void initialiserInventaireVide(int idPersonnage);

    /**
     * Vide entièrement l'inventaire d'un personnage.
     *
     * @param idPersonnage ID du personnage.
     */
    void viderInventaire(int idPersonnage);

    /**
     * Récupère le contenu du coffre d'un personnage.
     *
     * @param idPersonnage ID du personnage.
     * @return Liste des items contenus dans le coffre.
     */
    List<Item> recupererContenuCoffre(int idPersonnage);

    /**
     * Ajoute un item dans le coffre d'un personnage.
     *
     * @param item         Item à ajouter.
     * @param idPersonnage ID du personnage.
     * @return true si succès, false sinon.
     */
    boolean ajouterItemDansCoffre(Item item, int idPersonnage);

    /**
     * Supprime un item du coffre d'un personnage par son ID.
     *
     * @param itemId       ID de l'item.
     * @param idPersonnage ID du personnage.
     * @return true si l'item a été supprimé, false sinon.
     */
    boolean supprimerItemDuCoffre(String itemId, int idPersonnage);
}
