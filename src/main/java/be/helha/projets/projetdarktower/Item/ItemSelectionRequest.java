package be.helha.projets.projetdarktower.Item;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemSelectionRequest {
    @NotBlank(message = "L'ID de l'item ne peut pas être vide")
    private String itemId;
    private String cibleId;  // Ce champ est optionel

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCibleId() {
        return cibleId;
    }

    public void setCibleId(String cibleId) {
        this.cibleId = cibleId;
    }
}
