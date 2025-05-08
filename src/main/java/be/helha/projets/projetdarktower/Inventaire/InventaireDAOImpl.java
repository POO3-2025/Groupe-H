package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.Item.Weapon;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemFactory;
import be.helha.projets.projetdarktower.Item.Potion;
import be.helha.projets.projetdarktower.Item.Coffre;
import be.helha.projets.projetdarktower.Model.Personnage;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InventaireDAOImpl implements InventaireDAO {
    private static final int TAILLE_MAX = 10;
    private final List<Item> emplacements = new ArrayList<>();
    private final MongoCollection<Document> collection;

    public InventaireDAOImpl() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("Game");
        this.collection = database.getCollection("Inventaire");
    }

    // Vérifie si l'utilisateur a déjà un coffre dans son inventaire (MongoDB)
    public boolean hasCoffreInInventory(String userId) {
        Document query = new Document("userId", userId);
        Document userInventory = collection.find(query).first();

        if (userInventory != null) {
            List<Document> items = (List<Document>) userInventory.get("items");
            for (Document item : items) {
                if ("Coffre".equals(item.getString("type"))) {
                    return true; // L'utilisateur a un coffre
                }
            }
        }
        return false;
    }


    // Méthode pour ajouter un item à l'inventaire
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
            String nom = doc.getString("nom");
            Item item = ItemFactory.creerItem(nom);
            item.setId(doc.getString("_id"));
            return item;
        }
        return null;
    }

    // Méthode d'utilisation d'un item (Potion, Weapon, etc.)
    public String UseItem(Item item, Personnage utilisateur, Personnage cible) {
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            if (cible != null) {
                int degats = weapon.getDegats();
                return "L'utilisateur " + utilisateur.getNom() + " attaque la cible " + cible.getNom() + " avec l'épée " + weapon.getNom() + " infligeant " + degats + " dégâts.";
            } else {
                return "Cible non spécifiée pour l'attaque.";
            }
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            int pointsDeVie = potion.getPointsDeVieRecuperes() + utilisateur.getPointsDeVie();
            utilisateur.setPointsDeVie(pointsDeVie);
            return "L'utilisateur " + utilisateur.getNom() + " utilise la potion " + potion.getNom() + " et récupère " + potion.getPointsDeVieRecuperes() + " points de vie.";
        }
        return "Cet objet ne peut pas être utilisé.";
    }

    // Convertit un item en document MongoDB pour l'insertion
    private Document toDocument(Item item) {
        return new Document()
                .append("_id", item.getId())
                .append("nom", item.getNom())
                .append("type", item.getType());
    }
}
