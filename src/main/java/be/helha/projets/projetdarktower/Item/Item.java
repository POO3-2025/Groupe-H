package be.helha.projets.projetdarktower.Item;

import be.helha.projets.projetdarktower.Model.Personnage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.bson.types.ObjectId;

/**
 * Classe de base représentant un item générique dans le jeu.
 *
 * <p>Cette classe est la super-classe des types concrets comme Weapon, Potion, Coffre.
 * Elle supporte la (dé)sérialisation polymorphique JSON via Jackson.</p>
 *
 * <p>Chaque item a un identifiant unique généré automatiquement (format ObjectId sous forme String),
 * un nom, un type et une chance de drop exprimée en pourcentage.</p>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Weapon.class, name = "Weapon"),
        @JsonSubTypes.Type(value = Potion.class, name = "Potion"),
        @JsonSubTypes.Type(value = Coffre.class, name = "Coffre")
})
public class Item {

    /**
     * Identifiant unique de l'item au format String (généré via ObjectId MongoDB).
     */
    @JsonProperty("_id")
    protected String id;

    /**
     * Nom de l'item.
     */
    protected String nom;

    /**
     * Type d'item (ex: Weapon, Potion, Coffre).
     */
    protected String type;

    /**
     * Pourcentage de chance que cet item soit droppé (0.0 à 100.0).
     */
    protected double chanceDeDrop;

    /**
     * Constructeur principal.
     * Génère automatiquement un ID unique et initialise le nom, le type et la chance de drop.
     *
     * @param nom          Nom de l'item.
     * @param chanceDeDrop Pourcentage de chance de drop.
     */
    public Item(String nom, Double chanceDeDrop) {
        this.nom = nom;
        this.type = "Item";
        this.id = new ObjectId().toString();
        this.chanceDeDrop = chanceDeDrop;
    }

    /**
     * Constructeur par défaut requis pour la désérialisation JSON.
     */
    public Item() {
    }

    /**
     * Retourne l'identifiant unique de l'item.
     *
     * @return ID sous forme de String.
     */
    public String getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'item.
     *
     * @param id ID à assigner.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retourne le nom de l'item.
     *
     * @return nom de l'item.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de l'item.
     *
     * @param nom nouveau nom.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le type de l'item.
     *
     * @return type d'item.
     */
    public String getType() {
        return type;
    }

    /**
     * Définit le type de l'item.
     *
     * @param type nouveau type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retourne la chance de drop de l'item en pourcentage.
     *
     * @return chance de drop (0.0 à 100.0).
     */
    public double getChanceDeDrop() {
        return chanceDeDrop;
    }

    /**
     * Définit la chance de drop de l'item.
     *
     * @param chanceDeDrop nouvelle chance de drop (0.0 à 100.0).
     */
    public void setChanceDeDrop(double chanceDeDrop) {
        this.chanceDeDrop = chanceDeDrop;
    }

    /**
     * Représentation textuelle de l'item, affichant ses attributs principaux.
     *
     * @return chaîne descriptive.
     */
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
