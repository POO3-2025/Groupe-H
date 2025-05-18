package be.helha.projets.projetdarktower.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.Document;

/**
 * Représente un coffre, un type spécial d'item pouvant contenir plusieurs autres items.
 *
 * <p>Le coffre a une capacité maximale fixe (10 items).
 * Il ne peut pas contenir d'autres coffres.</p>
 */
public class Coffre extends Item {
    private static final int CAPACITE_MAX = 10;
    private final List<Item> contenu;

    /**
     * Constructeur avec nom et chance de drop.
     * Initialise un coffre vide.
     *
     * @param nom          Nom du coffre.
     * @param chanceDeDrop Chance de drop.
     */
    public Coffre(String nom, double chanceDeDrop) {
        super(nom, chanceDeDrop);
        this.type = "Coffre";
        this.contenu = new ArrayList<>();
    }

    /**
     * Constructeur par défaut.
     * Initialise un coffre vide avec type "Coffre".
     */
    public Coffre() {
        super();
        this.type = "Coffre";
        this.contenu = new ArrayList<>();
    }

    /**
     * Retourne une copie de la liste des items contenus dans le coffre.
     *
     * @return Liste des items dans le coffre.
     */
    public List<Item> getContenu() {
        return new ArrayList<>(contenu);
    }

    /**
     * Ajoute un item dans le coffre si la capacité maximale n'est pas atteinte
     * et si l'item n'est pas lui-même un coffre.
     *
     * @param item Item à ajouter.
     * @return true si ajouté avec succès, false sinon.
     */
    public boolean ajouterItem(Item item) {
        if (estPlein() || (item instanceof Coffre)) return false;
        return contenu.add(item);
    }

    /**
     * Retire un item du coffre selon son index.
     *
     * @param index Position de l'item à retirer.
     * @return true si l'item a été retiré, false si index invalide.
     */
    public boolean retirerItem(int index) {
        if (index < 0 || index >= contenu.size()) return false;
        contenu.remove(index);
        return true;
    }

    /**
     * Indique si le coffre est plein (a atteint la capacité maximale).
     *
     * @return true si plein, false sinon.
     */
    public boolean estPlein() {
        return contenu.size() >= CAPACITE_MAX;
    }

    /**
     * Indique si le coffre est vide.
     *
     * @return true si vide, false sinon.
     */
    public boolean estVide() {
        return contenu.isEmpty();
    }

    /**
     * Retourne une représentation textuelle du coffre et de son contenu.
     *
     * @return Description textuelle.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Coffre {\n");
        for (int i = 0; i < CAPACITE_MAX; i++) {
            sb.append("  ")
                    .append(i + 1)
                    .append(": ")
                    .append(i < contenu.size() ? contenu.get(i) : "[vide]")
                    .append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convertit ce coffre en un Document MongoDB, incluant son contenu.
     *
     * @return Document représentant le coffre.
     */
    public Document toDocument() {
        List<Document> contenuDocs = new ArrayList<>();
        for (Item item : contenu) {
            contenuDocs.add(new Document()
                    .append("_id", item.getId())
                    .append("nom", item.getNom())
                    .append("type", item.getType()));
        }

        return new Document()
                .append("_id", this.getId())
                .append("nom", this.nom)
                .append("type", this.type)
                .append("contenu", contenuDocs);
    }

    /**
     * Convertit un item en Document MongoDB.
     * Utile pour enregistrer un item (weapon, potion, coffre).
     *
     * @param item Item à convertir.
     * @return Document MongoDB.
     */
    private Document toDocument(Item item) {
        Document doc = new Document()
                .append("_id", item.getId())
                .append("nom", item.getNom())
                .append("type", item.getType())
                .append("chanceDeDrop", item.getChanceDeDrop());

        if (item instanceof Weapon weapon) {
            doc.append("degats", weapon.getDegats());
        } else if (item instanceof Potion potion) {
            doc.append("pointsDeVieRecuperes", potion.getPointsDeVieRecuperes());
        } else if (item instanceof Coffre) {
            List<Object> contenuVide = new ArrayList<>(Collections.nCopies(CAPACITE_MAX, null));
            doc.append("contenu", contenuVide);
        }

        return doc;
    }
}
