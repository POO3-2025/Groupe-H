package be.helha.projets.projetdarktower.Item;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

/**
 * Requête utilisée pour sélectionner un item dans une opération (ex: utilisation d'item).
 *
 * <p>Contient l'identifiant obligatoire de l'item, et l'identifiant optionnel d'une cible.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemSelectionRequest {

    /**
     * Identifiant unique de l'item à utiliser.
     * Doit être non vide.
     */
    @NotBlank(message = "L'ID de l'item ne peut pas être vide")
    private String itemId;

    /**
     * Identifiant optionnel de la cible sur laquelle l'item est utilisé.
     */
    private String cibleId;

    /**
     * Retourne l'identifiant de l'item.
     * @return ID de l'item.
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Définit l'identifiant de l'item.
     * @param itemId ID à définir.
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * Retourne l'identifiant de la cible.
     * @return ID de la cible, ou null si aucune.
     */
    public String getCibleId() {
        return cibleId;
    }

    /**
     * Définit l'identifiant de la cible.
     * @param cibleId ID de la cible.
     */
    public void setCibleId(String cibleId) {
        this.cibleId = cibleId;
    }
}
