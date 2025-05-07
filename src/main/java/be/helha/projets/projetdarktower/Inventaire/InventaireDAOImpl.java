package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.Item.Weapon;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemFactory;
import be.helha.projets.projetdarktower.Item.Potion;
import be.helha.projets.projetdarktower.Item.Coffre;
import be.helha.projets.projetdarktower.Model.Personnage;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.stereotype.Repository;  // Ajoutez l'annotation @Repository

import java.util.ArrayList;
import java.util.List;

@Repository  // Annotation pour indiquer que c'est un bean Spring
public class InventaireDAOImpl implements InventaireDAO {
    private static final int TAILLE_MAX = 10;
    private final List<Item> emplacements = new ArrayList<>();
    private final MongoCollection<Document> collection;

    public InventaireDAOImpl() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("Game"); // Mets ici le vrai nom
        this.collection = database.getCollection("Inventaire");
    }

    public boolean ajouterItem(Item item) {
        // Vérifie si l'inventaire contient déjà un Coffre
        if (item instanceof Coffre) {
            for (Item i : emplacements) {
                if (i instanceof Coffre) {
                    System.out.println("Un seul coffre est autorisé !");
                    return false;
                }
            }
        }

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

    // Dans InventaireDAOImpl
    public String UseItem(Item item, Personnage utilisateur, Personnage cible) {
        if (item instanceof Weapon) {
            // Si c'est une épée, l'attaque est effectuée sur la cible si elle est présente
            Weapon weapon = (Weapon) item;
            if (cible != null) {
                int degats = weapon.getDegats(); // Dégâts infligés à la cible
                return "L'utilisateur " + utilisateur.getNom() + " attaque la cible " + cible.getNom() + " avec l'épée " + weapon.getNom() + " infligeant " + degats + " dégâts.";
            } else {
                return "Cible non spécifiée pour l'attaque.";
            }
        } else if (item instanceof Potion) {
            // Si c'est une potion, on soigne l'utilisateur
            Potion potion = (Potion) item;
            int pointsDeVie = potion.getPointsDeVieRecuperes() + utilisateur.getPointsDeVie();
            utilisateur.setPointsDeVie(pointsDeVie);
            return "L'utilisateur " + utilisateur.getNom() + " utilise la potion " + potion.getNom() + " et récupère " + potion.getPointsDeVieRecuperes() + " points de vie.";
        }
        return "Cet objet ne peut pas être utilisé.";
    }


    private Document toDocument(Item item) {
        return new Document()
                .append("_id", item.getId())
                .append("nom", item.getNom())
                .append("type", item.getType());
    }
}
