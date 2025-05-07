package be.helha.projets.projetdarktower.Item;

import be.helha.projets.projetdarktower.Model.Personnage;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class Item {
    @JsonProperty("_id")
    protected String id;
    protected String nom;
    protected String type;
    protected double chanceDeDrop; // Pourcentage de chance (entre 0.0 et 100.0)

    public Item(String nom,Double chanceDeDrop) {
        this.nom = nom;
        this.type = "Item";
        this.id = UUID.randomUUID().toString();
        this.chanceDeDrop = chanceDeDrop; // Valeur par défaut (à définir selon l'objet)
    }

    public static class ObjectIdWrapper {
        @JsonProperty("$oid")
        private String oid;

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        @Override
        public String toString() {
            return oid;
        }
    }

    // Getters et setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public double getChanceDeDrop() {
        return chanceDeDrop;
    }

    public void setChanceDeDrop(double chanceDeDrop) {
        this.chanceDeDrop = chanceDeDrop;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", chanceDeDrop=" + chanceDeDrop +
                '}';
    }
}
