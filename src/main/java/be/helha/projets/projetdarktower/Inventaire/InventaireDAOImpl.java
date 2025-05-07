package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemFactory;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.stereotype.Repository;  // Ajoutez l'annotation @Repository

import java.util.ArrayList;
import java.util.List;

@Repository  // Annotation pour indiquer que c'est un bean Spring
public class InventaireDAO {
    private static final int TAILLE_MAX = 10;
    private final List<Item> emplacements = new ArrayList<>();
    private final MongoCollection<Document> collection;

    public InventaireDAO() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("Game"); // Mets ici le vrai nom
        this.collection = database.getCollection("Inventaire");
    }

    public boolean ajouterItem(Item item) {
        if (emplacements.size() < TAILLE_MAX) {
            emplacements.add(item);
            collection.insertOne(toDocument(item));
            return true;
        }
        return false;
    }

    public List<Item> chargerInventaire() {
        List<Item> inventaire = new ArrayList<>();
        for (Document doc : collection.find()) {
            String type = doc.getString("type");
            String nom = doc.getString("nom");

            Item item = ItemFactory.creerItem(nom);
            item.setId(doc.getString("_id"));
            inventaire.add(item);
        }
        return inventaire;
    }

    public Item recupererItemParId(String itemId) {
        Document doc = collection.find(new Document("_id", itemId)).first();
        if (doc != null) {
            String type = doc.getString("type");
            String nom = doc.getString("nom");

            Item item = ItemFactory.creerItem(nom);
            item.setId(doc.getString("_id"));
            return item;
        }
        return null;
    }

    private Document toDocument(Item item) {
        return new Document()
                .append("_id", item.getId())
                .append("nom", item.getNom())
                .append("type", item.getType());
    }
}
