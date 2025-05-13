package be.helha.projets.projetdarktower.Inventaire;

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
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class InventaireDAOImpl implements InventaireDAO {
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
        Document doc = collection.find(new Document("_id", itemId)).first();
        if (doc != null) {
            String nom = doc.getString("nom");
            Item item = ItemFactory.creerItem(nom);
            item.setId(doc.getString("_id"));
            return item;
        }
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
    private void decrementUsageInMongo(String itemId) {
        Document filter = new Document("items._id", itemId);

        Document update = new Document("$inc",
                new Document("items.$[elem].Usage_Time", -1));

        List<Bson> arrayFilters = List.of(
                Filters.eq("elem._id", itemId)
        );

        UpdateOptions options = new UpdateOptions().arrayFilters(arrayFilters);

        collection.updateOne(filter, update, options);
    }





    // Méthode d'utilisation d'un item (Potion, Weapon, etc.)
    public String UseItem(Item item, Personnage utilisateur, Personnage cible) {
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            if (cible != null) {
                int degats = weapon.getDegats();
                int vieRestante = cible.getPointsDeVie() - degats;
                cible.setPointsDeVie(vieRestante);

                // Gérer la durabilité


                // MAJ dans MongoDB
                decrementUsageInMongo(weapon.getId());

                int durabilite = RecupererUsageItem(weapon.getId());
                // Si l'arme n'a plus d'utilisations, on la supprime
                if (durabilite <= 0) {
                    DeleteItem(weapon.getId());
                }

                return "L'utilisateur " + utilisateur.getNom() + " attaque la cible " + cible.getNom() + " avec l'épée " + weapon.getNom() + " infligeant " + degats + " dégâts.";
            } else {
                return "Cible non spécifiée pour l'attaque.";
            }
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            int pointsDeVie = potion.getPointsDeVieRecuperes() + utilisateur.getPointsDeVie();
            utilisateur.setPointsDeVie(pointsDeVie);

            decrementUsageInMongo(item.getId());
            int durabilite = RecupererUsageItem(potion.getId());
            if (durabilite <= 0) {
                DeleteItem(potion.getId());
            }
                decrementUsageInMongo(item.getId());

            return "L'utilisateur " + utilisateur.getNom() + " utilise la potion " + potion.getNom() + " et récupère " + potion.getPointsDeVieRecuperes() + " points de vie.";
        }

        return "Cet objet ne peut pas être utilisé.";
    }

    private int RecupererUsageItem(String itemId) {
        Document inventory = collection.find(new Document("items._id", itemId)).first();
        if (inventory != null) {
            List<Document> items = (List<Document>) inventory.get("items");
            for (Document item : items) {
                if (itemId.equals(item.getString("_id"))) {
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

}
