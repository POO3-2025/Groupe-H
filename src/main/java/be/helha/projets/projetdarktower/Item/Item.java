package be.helha.projets.projetdarktower.Item;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class Item {
    @JsonProperty("_id")
    protected ObjectId id;
    protected String nom;
    protected String type;

    public Item(String nom) {
        this.nom = nom;
        this.type = "Épée";
        this.id = new ObjectId();
    }

    public static class ObjectIdWrapper {
        @JsonProperty("$oid")
        private String oid;

        public String getOid() { return oid; }
        public void setOid(String oid) { this.oid = oid; }

        @Override
        public String toString() {
            return oid;
        }
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
