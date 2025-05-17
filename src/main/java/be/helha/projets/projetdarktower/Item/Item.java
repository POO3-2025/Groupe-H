package be.helha.projets.projetdarktower.Item;

import be.helha.projets.projetdarktower.Model.Personnage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.bson.types.ObjectId; // Importation de ObjectId pour MongoDB

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // le champ JSON qui indique le type concret
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Weapon.class, name = "Weapon"),
        @JsonSubTypes.Type(value = Potion.class, name = "Potion"),
        @JsonSubTypes.Type(value = Coffre.class, name = "Coffre")
})
public class Item {
    @JsonProperty("_id")
    protected String id; // ID sera désormais un ObjectId en format String
    protected String nom;
    protected String type;
    protected double chanceDeDrop; // Pourcentage de chance (entre 0.0 et 100.0)

    // Constructeur
    public Item(String nom, Double chanceDeDrop) {
        this.nom = nom;
        this.type = "Item";
        this.id = new ObjectId().toString(); // Utilisation d'ObjectId pour générer un ID valide
        this.chanceDeDrop = chanceDeDrop; // Valeur par défaut (à définir selon l'objet)
    }
    public Item() {
        // Constructeur par défaut requis pour Jackson
    }

    // Getter et setter pour id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters et setters pour nom, type, chanceDeDrop
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
