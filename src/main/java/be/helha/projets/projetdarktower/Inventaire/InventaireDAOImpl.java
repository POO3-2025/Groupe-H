package be.helha.projets.projetdarktower.Inventaire;

import be.helha.projets.projetdarktower.DBConnection.DatabaseConnection;
import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Item.Weapon;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemFactory;
import be.helha.projets.projetdarktower.Item.Potion;
import be.helha.projets.projetdarktower.Item.Coffre;
import be.helha.projets.projetdarktower.Model.Personnage;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class InventaireDAOImpl implements InventaireDAO {
    private final List<Item> emplacements = new ArrayList<>();
    private final MongoCollection<Document> collection;

    public InventaireDAOImpl() {
        // Utiliser DatabaseConnection singleton pour obtenir MongoDatabase
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        MongoDatabase database;
        try {
            database = dbConn.getMongoDatabase("MongoDBProduction");
        } catch (RuntimeException e) {
            throw new RuntimeException("Impossible de se connecter à MongoDB: " + e.getMessage());
        }

        this.collection = database.getCollection("Inventaire");
    }

    public boolean hasCoffreInInventory(int idPersonnage) {
        Document query = new Document("idPersonnage", idPersonnage);
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
    public boolean ajouterItem(Item item, int idPersonnage) {
        Document query = new Document("idPersonnage", idPersonnage);
        Document inventaireDoc = collection.find(query).first();

        if (inventaireDoc == null) {
            System.out.println("Aucun inventaire trouvé pour ce personnage.");
            return false;
        }

        List<Object> items = (List<Object>) inventaireDoc.get("items");

        // Vérifie qu’il n’y ait pas déjà un coffre
        if (item instanceof Coffre) {
            for (Object obj : items) {
                if (obj instanceof Document) {
                    Document doc = (Document) obj;
                    if ("Coffre".equals(doc.getString("type"))) {
                        System.out.println("Un seul coffre est autorisé !");
                        return false;
                    }
                }
            }
        }

        // Recherche du premier slot vide (null)
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                items.set(i, toDocument(item)); // Insertion de l’item
                // Mise à jour dans MongoDB
                Document update = new Document("$set", new Document("items", items));
                collection.updateOne(query, update);
                return true;
            }
        }

        System.out.println("L'inventaire est plein !");
        return false;
    }


    public List<Item> chargerInventaire(int idPersonnage) {
        List<Item> inventaire = new ArrayList<>();

        // Création de la requête pour récupérer l'inventaire du personnage avec l'ID spécifié
        Document query = new Document("idPersonnage", idPersonnage);

        // Recherche de l'inventaire dans MongoDB en utilisant l'idPersonnage
        Document userInventory = collection.find(query).first();

        // Vérifie si l'inventaire existe
        if (userInventory != null && userInventory.containsKey("items")) {
            Object slotsObj = userInventory.get("items");

            if (slotsObj instanceof List<?>) {
                List<?> rawList = (List<?>) slotsObj;

                for (Object obj : rawList) {
                    if (obj instanceof Document) {
                        Document itemDoc = (Document) obj;
                        String nom = itemDoc.getString("nom");

                        if (nom != null) {
                            Item item = ItemFactory.creerItem(nom);
                            item.setId(itemDoc.getString("_id")); // Associe l'ID de l'item
                            inventaire.add(item); // Ajoute l'item à l'inventaire
                        }
                    }
                }
            }
        }

        return inventaire;
    }




    public Item recupererItemParId(String itemId) {
        System.out.println("Recherche de l'item avec l'ID : " + itemId);

        Document match = collection.find(new Document("items._id", itemId)).first();

        if (match != null) {
            List<Document> items = (List<Document>) match.get("items");
            for (Document doc : items) {
                if (doc != null && itemId.equals(doc.getString("_id"))) { // Vérifie que l'item n'est pas null
                    String nom = doc.getString("nom");
                    Item item = ItemFactory.creerItem(nom);
                    item.setId(doc.getString("_id"));
                    return item;
                }
            }
        }

        System.out.println("Aucun item trouvé avec cet ID.");
        return null;
    }






    public String DeleteItem(String itemId) {
        // On parcourt chaque inventaire pour chercher l'item
        FindIterable<Document> allInventories = collection.find();

        for (Document inventory : allInventories) {
            List<Object> items = (List<Object>) inventory.get("items");

            for (int i = 0; i < items.size(); i++) {
                Object obj = items.get(i);
                if (obj instanceof Document) {
                    Document itemDoc = (Document) obj;
                    String id = itemDoc.getString("_id");

                    if (itemId.equals(id)) {
                        // Remplace l'item par null
                        items.set(i, null);

                        // Met à jour l'inventaire dans MongoDB
                        Document filter = new Document("_id", inventory.getObjectId("_id"));
                        Document update = new Document("$set", new Document("items", items));
                        collection.updateOne(filter, update);

                        return "Item avec l'ID " + itemId + " supprimé avec succès.";
                    }
                }
            }
        }

        return "Aucun item trouvé avec l'ID " + itemId + ".";
    }

    //MAJ LA DB APRES CHAQUE UTILISATION
    // Dans la méthode `UseItem` de la classe `InventaireDAOImpl`
    private void decrementUsageInMongo(String itemId) {
        Document filter = new Document("items._id", itemId);

        // Décrémentation de l'usage de l'item
        Document update = new Document("$inc", new Document("items.$[elem].Usage_Time", -1));

        List<Bson> arrayFilters = List.of(Filters.eq("elem._id", itemId));

        UpdateOptions options = new UpdateOptions().arrayFilters(arrayFilters);

        // Appliquer la mise à jour dans MongoDB
        collection.updateOne(filter, update, options);
    }






    // Méthode d'utilisation d'un item (Potion, Weapon, etc.)

    @Override
    public UseItemResult UseItem(Item item, Personnage utilisateur, Personnage cible) {
        String message;
        String itemSupprimeId = null;

        if (item instanceof Potion) {
            Potion potion = (Potion) item;
            int pointsRecuperes = potion.getPointsDeVieRecuperes();

            message = "L'utilisateur " + utilisateur.getNom() + " utilise la potion " + potion.getNom() +
                    " et récupère " + pointsRecuperes + " points de vie.";
            itemSupprimeId = item.getId();

            DeleteItem(item.getId());
            // Retourne les PV actuels de la cible sans modifier les PV du Minotaure
            return new UseItemResult(message, 0,
                    pointsRecuperes, itemSupprimeId);
        }

        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            if (cible != null) {
                int degats = weapon.getDegats();

                message = "L'utilisateur " + utilisateur.getNom() + " attaque " + cible.getNom() +
                        " avec " + weapon.getNom() + " et inflige " + degats + " dégâts.";
                itemSupprimeId = null;

                // Décrémenter la durabilité de l'arme
                decrementUsageInMongo(item.getId());
                int usages = RecupererUsageItem(item.getId());
                weapon.setUsages(weapon.getUsages() - 1); // Mise à jour locale des usages

                // Vérifie si les usages sont à 0 et supprime l'item
                if (usages <= 0) {
                    DeleteItem(item.getId());
                    itemSupprimeId = item.getId(); // Indique que l'item a été supprimé
                    message += " L'arme " + weapon.getNom() + " est cassée et a été supprimée.";
                }
            } else {
                message = "Aucune cible spécifiée pour l'attaque.";
            }

            return new UseItemResult(message, weapon.getDegats(),
                    0, itemSupprimeId);
        }

        return new UseItemResult("Type d'item non supporté.", 0,
                0, null);
    }

    private int RecupererUsageItem(String itemId) {
        Document inventory = collection.find(new Document("items._id", itemId)).first();
        if (inventory != null) {
            List<Document> items = (List<Document>) inventory.get("items");
            for (Document item : items) {
                if (item != null && itemId.equals(item.getString("_id"))) { // Vérifie que l'item n'est pas null
                    return item.getInteger("Usage_Time", 0);
                }
            }
        }
        return 0; // Valeur par défaut si non trouvé
    }




    public void initialiserInventaireVide(int idPersonnage) {
        // Vérifie si l'inventaire existe déjà
        Document existing = collection.find(new Document("idPersonnage", idPersonnage)).first();
        if (existing != null) {
            System.out.println("Inventaire déjà initialisé pour ce personnage.");
            return;
        }

        // Crée une liste de 10 slots vides
        List<Object> items = new ArrayList<>(Collections.nCopies(10, null));

        Document inventaireDoc = new Document()
                .append("idPersonnage", idPersonnage)
                .append("items", items);

        collection.insertOne(inventaireDoc);
        System.out.println("Inventaire vide initialisé pour le personnage " + idPersonnage);
    }

    public void viderInventaire(int idPersonnage) {
        Document query = new Document("idPersonnage", idPersonnage);
        Document existing = collection.find(query).first();

        if (existing == null) {
            System.out.println("Aucun inventaire trouvé pour le personnage " + idPersonnage);
            return;
        }

        Document update = new Document("$set", new Document("items", Collections.nCopies(10, null)));
        collection.updateOne(query, update);
        System.out.println("Inventaire vidé pour le personnage " + idPersonnage);
    }


    // Convertit un item en document MongoDB pour l'insertion
    private Document toDocument(Item item) {
        Document doc = new Document()
                .append("_id", item.getId())
                .append("nom", item.getNom())
                .append("type", item.getType());

        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            doc.append("degats", weapon.getDegats());
            doc.append("Usage_Time",weapon.getUsages());
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            doc.append("pointsDeVieRecuperes", potion.getPointsDeVieRecuperes());
            doc.append("Usage_Time",potion.getUsages());
        } else if (item instanceof Coffre) {
            // Coffre vide avec 10 emplacements = liste de 10 null
            List<Object> contenuVide = new ArrayList<>(Collections.nCopies(10, null));
            doc.append("contenu", contenuVide);
        }

        return doc;
    }
    public List<Item> recupererContenuCoffre(int idPersonnage) {
        Document query = new Document("idPersonnage", idPersonnage);
        Document inventaireDoc = collection.find(query).first();

        if (inventaireDoc == null) return new ArrayList<>();

        List<Document> items = (List<Document>) inventaireDoc.get("items");
        for (Document item : items) {
            if (item != null && "Coffre".equals(item.getString("type"))) {
                List<Item> contenu = new ArrayList<>();
                List<Object> rawContenu = (List<Object>) item.get("contenu");
                if (rawContenu == null) continue;
                for (Object obj : rawContenu) {
                    if (obj instanceof Document doc) {
                        String nom = doc.getString("nom");
                        Item it = ItemFactory.creerItem(nom);
                        it.setId(doc.getString("_id"));
                        contenu.add(it);
                    }
                }
                return contenu;
            }
        }

        return new ArrayList<>();
    }
    public boolean ajouterItemDansCoffre(Item item, int idPersonnage) {
        Document query = new Document("idPersonnage", idPersonnage);
        Document inventaireDoc = collection.find(query).first();

        if (inventaireDoc == null) return false;

        List<Document> items = (List<Document>) inventaireDoc.get("items");

        for (Document doc : items) {
            if (doc != null && "Coffre".equals(doc.getString("type"))) {
                List<Object> contenu = (List<Object>) doc.get("contenu");
                for (int i = 0; i < contenu.size(); i++) {
                    if (contenu.get(i) == null) {
                        contenu.set(i, toDocument(item));
                        collection.updateOne(query, new Document("$set", new Document("items", items)));
                        return true;
                    }
                }
                return false; // Pas de place
            }
        }

        return false; // Pas de coffre
    }
    public boolean supprimerItemDuCoffre(String itemId, int idPersonnage) {
        Document query = new Document("idPersonnage", idPersonnage);
        Document inventaireDoc = collection.find(query).first();

        if (inventaireDoc == null) return false;

        List<Document> items = (List<Document>) inventaireDoc.get("items");

        for (Document doc : items) {
            if (doc != null && "Coffre".equals(doc.getString("type"))) {
                List<Object> contenu = (List<Object>) doc.get("contenu");
                for (int i = 0; i < contenu.size(); i++) {
                    Object obj = contenu.get(i);
                    if (obj instanceof Document docItem && itemId.equals(docItem.getString("_id"))) {
                        contenu.set(i, null);
                        collection.updateOne(query, new Document("$set", new Document("items", items)));
                        return true;
                    }
                }
            }
        }

        return false;
    }



}
