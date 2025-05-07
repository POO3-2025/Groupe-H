package be.helha.projets.projetdarktower.Item;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;


public class Coffre extends Item {
    private static final int CAPACITE_MAX = 5;
    private final List<Item> contenu;

    public Coffre(String nom,double chanceDeDrop) {
        super(nom, chanceDeDrop);
        this.nom = nom;
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
    public static Coffre fromDocument(Document doc) {
        String nomCoffre = doc.getString("nom");
        Double chanceDeDrop = doc.getDouble("chanceDeDrop");
        Coffre coffre = new Coffre(nomCoffre,chanceDeDrop);
        coffre.setId(doc.getString("_id"));  // Utilisation de l'ID sous forme de String

        List<Document> contenuDocs = doc.getList("contenu", Document.class, new ArrayList<>());
        for (Document itemDoc : contenuDocs) {
            String nomItem = itemDoc.getString("nom");
            String typeItem = itemDoc.getString("type");

            // Utilisation de la factory pour créer l’objet Item adapté
            Item item = ItemFactory.creerItem(nomItem);  // Appel à la méthode avec nom de l'item
            item.setId(itemDoc.getString("_id")); // Properly set the ID for each item

            coffre.ajouterItem(item); // Add to coffre's content
        }
        return coffre;
    }
}
