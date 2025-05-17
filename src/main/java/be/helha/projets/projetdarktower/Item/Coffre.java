package be.helha.projets.projetdarktower.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.Document;


public class Coffre extends Item {
    private static final int CAPACITE_MAX = 10;
    private final List<Item> contenu;

    public Coffre(String nom,double chanceDeDrop) {
        super(nom, chanceDeDrop);
        this.nom = nom;
        this.type = "Coffre";
        this.contenu = new ArrayList<>();
    }
    public Coffre() {
        super();
        this.type = "Coffre";
        this.contenu = new ArrayList<>();
    }

    public boolean ajouterItem(Item item) {
        if (contenu.size() >= CAPACITE_MAX) return false;
        if (item instanceof Coffre) return false;
        contenu.add(item);
        return true;
    }

    public boolean retirerItem(int index) {
        if (index >= 0 && index < contenu.size()) {
            contenu.remove(index);
            return true;
        }
        return false;
    }

    public List<Item> getContenu() {
        return new ArrayList<>(contenu);
    }

    public boolean estPlein() {
        return contenu.size() >= CAPACITE_MAX;
    }

    public boolean estVide() {
        return contenu.isEmpty();
    }

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

    // Méthode toDocument : convertit le Coffre en Document MongoDB
    public Document toDocument() {
        List<Document> contenuDocs = new ArrayList<>();
        for (Item item : contenu) {
            contenuDocs.add(new Document()
                    .append("_id", item.getId())
                    .append("nom", item.getNom())
                    .append("type", item.getType()));
        }

        return new Document()
                .append("_id", this.getId())  // Utilisation de l'ID généré
                .append("nom", this.nom)
                .append("type", this.type)
                .append("contenu", contenuDocs);
    }

    // Méthode fromDocument : crée un Coffre à partir d’un Document MongoDB
    private Document toDocument(Item item) {
        Document doc = new Document()
                .append("_id", item.getId())
                .append("nom", item.getNom())
                .append("type", item.getType())
                .append("chanceDeDrop", item.getChanceDeDrop());

        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            doc.append("degats", weapon.getDegats());
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            doc.append("pointsDeVieRecuperes", potion.getPointsDeVieRecuperes());
        } else if (item instanceof Coffre) {
            // Coffre vide avec 10 emplacements = liste de 10 null
            List<Object> contenuVide = new ArrayList<>(Collections.nCopies(10, null));
            doc.append("contenu", contenuVide);
        }

        return doc;
    }

}
