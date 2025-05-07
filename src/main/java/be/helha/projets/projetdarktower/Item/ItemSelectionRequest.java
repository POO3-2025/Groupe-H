package be.helha.projets.projetdarktower.Item;

public class ItemSelectionRequest {
    private String itemId;
    private String cibleId;  // Cible optionnelle

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
