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
            System.out.println("[hasCoffreInInventory] items size: " + (items == null ? "null" : items.size()));
            if (items != null) {
                for (Document item : items) {
                    System.out.println("[hasCoffreInInventory] item type: " + (item == null ? "null" : item.getString("type")));
                    if (item != null && "Coffre".equals(item.getString("type"))) {
                        System.out.println("[hasCoffreInInventory] Coffre trouvé");
                        return true;
                    }
                }
            }
        }
        System.out.println("[hasCoffreInInventory] Coffre non trouvé");
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




    public Item recupererItemParId(String itemId, int idPersonnage) {
        System.out.println("Recherche de l'item avec l'ID : " + itemId);

        // Récupère le document complet du personnage
        Document inventoryDoc = collection.find(new Document("idPersonnage", idPersonnage)).first();

        if (inventoryDoc != null) {
            List<Document> items = (List<Document>) inventoryDoc.get("items");

            // Chercher dans inventaire principal
            for (Document doc : items) {
                if (doc != null && itemId.equals(doc.getString("_id"))) {
                    String nom = doc.getString("nom");
                    Item item = ItemFactory.creerItem(nom);
                    item.setId(doc.getString("_id"));
                    return item;
                }
            }

            // Chercher dans contenu des coffres
            for (Document doc : items) {
                if (doc != null && "Coffre".equals(doc.getString("type"))) {
                    List<Document> contenu = (List<Document>) doc.get("contenu");
                    if (contenu != null) {
                        for (Document itemDoc : contenu) {
                            if (itemDoc != null && itemId.equals(itemDoc.getString("_id"))) {
                                String nom = itemDoc.getString("nom");
                                Item item = ItemFactory.creerItem(nom);
                                item.setId(itemDoc.getString("_id"));
                                return item;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Aucun item trouvé avec cet ID.");
        return null;
    }








    public String DeleteItem(String itemId) {
        // Parcourt chaque inventaire
        FindIterable<Document> allInventories = collection.find();

        for (Document inventory : allInventories) {
            List<Object> items = (List<Object>) inventory.get("items");

            boolean updated = false;

            // Cherche dans inventaire principal
            for (int i = 0; i < items.size(); i++) {
                Object obj = items.get(i);
                if (obj instanceof Document) {
                    Document itemDoc = (Document) obj;
                    String id = itemDoc.getString("_id");

                    if (itemId.equals(id)) {
                        // Supprime item principal
                        items.set(i, null);
                        updated = true;
                        break;
                    }

                    // Si item est un coffre, cherche dans contenu
                    if ("Coffre".equals(itemDoc.getString("type"))) {
                        List<Object> contenu = (List<Object>) itemDoc.get("contenu");
                        for (int j = 0; j < contenu.size(); j++) {
                            Object objContenu = contenu.get(j);
                            if (objContenu instanceof Document) {
                                Document docContenu = (Document) objContenu;
                                if (itemId.equals(docContenu.getString("_id"))) {
                                    // Supprime item dans coffre
                                    contenu.set(j, null);
                                    updated = true;
                                    break;
                                }
                            }
                        }
                        if (updated) break;
                    }
                }
            }

            if (updated) {
                // Met à jour la base
                Document filter = new Document("_id", inventory.getObjectId("_id"));
                Document update = new Document("$set", new Document("items", items));
                collection.updateOne(filter, update);
                return "Item avec l'ID " + itemId + " supprimé avec succès.";
            }
        }

        return "Aucun item trouvé avec l'ID " + itemId + ".";
    }

    //MAJ LA DB APRES CHAQUE UTILISATION
    // Dans la méthode `UseItem` de la classe `InventaireDAOImpl`
    private void decrementUsageInMongo(String itemId, int idPersonnage) {
        Document query = new Document("idPersonnage", idPersonnage);
        Document inventoryDoc = collection.find(query).first();

        if (inventoryDoc == null) {
            System.out.println("Inventaire non trouvé pour idPersonnage = " + idPersonnage);
            return;
        }

        List<Document> items = (List<Document>) inventoryDoc.get("items");
        boolean updated = false;

        for (Document doc : items) {
            if (doc != null && itemId.equals(doc.getString("_id"))) {
                int usage = doc.getInteger("Usage_Time", 0);
                // Ne pas décrémenter si Usage_Time est déjà à 0
                if (usage > 0) {
                    doc.put("Usage_Time", usage - 1); // Décrémenter Usage_Time
                    updated = true;
                }
                break;
            }
            // Si l'item est dans un coffre
            if (doc != null && "Coffre".equals(doc.getString("type"))) {
                List<Document> contenu = (List<Document>) doc.get("contenu");
                for (Document itemDoc : contenu) {
                    if (itemDoc != null && itemId.equals(itemDoc.getString("_id"))) {
                        int usage = itemDoc.getInteger("Usage_Time", 0);
                        if (usage > 0) {
                            itemDoc.put("Usage_Time", usage - 1); // Décrémenter Usage_Time
                            updated = true;
                        }
                        break;
                    }
                }
                if (updated) break;
            }
        }

        if (updated) {
            Document filter = new Document("_id", inventoryDoc.getObjectId("_id"));
            Document update = new Document("$set", new Document("items", items));
            collection.updateOne(filter, update);
            System.out.println("Inventaire mis à jour dans MongoDB pour itemId : " + itemId);
        } else {
            System.out.println("Item non trouvé pour décrément usage : " + itemId);
        }
    }










    // Méthode d'utilisation d'un item (Potion, Weapon, etc.)

    @Override
    public UseItemResult UseItem(Item item, Personnage utilisateur, Personnage cible, int UserId) {
        String message;
        String itemSupprimeId = null;

        if (item instanceof Potion) {
            Potion potion = (Potion) item;
            int pointsRecuperes = potion.getPointsDeVieRecuperes();

            message = "L'utilisateur " + utilisateur.getNom() + " utilise la potion " + potion.getNom() +
                    " et récupère " + pointsRecuperes + " points de vie.";
            itemSupprimeId = item.getId();

                DeleteItem(item.getId());



            return new UseItemResult(message, 0, pointsRecuperes, itemSupprimeId);
        }

        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            if (cible != null) {
                int degats = weapon.getDegats();

                message = "L'utilisateur " + utilisateur.getNom() + " attaque " + cible.getNom() +
                        " avec " + weapon.getNom() + " et inflige " + degats + " dégâts.";
                itemSupprimeId = null;

                // Décrémenter la durabilité de l'arme
                decrementUsageInMongo(item.getId(), UserId);

                int usagesRestants = RecupererUsageItem(item.getId(), UserId);
                weapon.setUsages(usagesRestants); // Mise à jour locale des usages

                // Vérifie si les usages sont à 0 et supprime l'item
                if (usagesRestants <= 0) {
                    DeleteItem(item.getId());
                    itemSupprimeId = item.getId(); // Indique que l'item a été supprimé
                    message += " L'arme " + weapon.getNom() + " est cassée et a été supprimée.";
                }
            } else {
                message = "Aucune cible spécifiée pour l'attaque.";
            }

            return new UseItemResult(message, weapon.getDegats(), 0, itemSupprimeId);
        }

        return new UseItemResult("Type d'item non supporté.", 0, 0, null);
    }


    private int RecupererUsageItem(String itemId, int idPersonnage) {
        // Cherche l'inventaire du personnage par ID
        Document query = new Document("idPersonnage", idPersonnage);
        Document inventory = collection.find(query).first();  // Recherche dans la collection d'inventaires

        if (inventory != null) {
            // Récupère la liste des items de l'inventaire
            List<Document> items = (List<Document>) inventory.get("items");

            // Cherche dans l'inventaire principal
            for (Document item : items) {
                if (item != null && itemId.equals(item.getString("_id"))) {
                    // Si l'item est trouvé, retourne son Usage_Time
                    return item.getInteger("Usage_Time", 0);  // Si Usage_Time n'existe pas, retourne 0
                }
            }

            // Si l'item n'est pas trouvé dans l'inventaire principal, cherche dans les coffres
            for (Document item : items) {
                if (item != null && "Coffre".equals(item.getString("type"))) {
                    // Si c'est un coffre, récupère le contenu du coffre
                    List<Document> contenu = (List<Document>) item.get("contenu");
                    if (contenu != null) {
                        for (Document itemDoc : contenu) {
                            // Cherche l'item dans le contenu du coffre
                            if (itemDoc != null && itemId.equals(itemDoc.getString("_id"))) {
                                // Si l'item est trouvé dans le coffre, retourne son Usage_Time
                                return itemDoc.getInteger("Usage_Time", 0);
                            }
                        }
                    }
                }
            }
        }
        // Si l'item n'est trouvé ni dans l'inventaire principal ni dans le coffre, retourne 0
        return 0;
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
    private Document toDocumentUsage(Item item, int usageTime) {
        Document doc = new Document()
                .append("_id", item.getId())
                .append("nom", item.getNom())
                .append("type", item.getType());

        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            doc.append("degats", weapon.getDegats());
            doc.append("Usage_Time", usageTime);
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            doc.append("pointsDeVieRecuperes", potion.getPointsDeVieRecuperes());
            doc.append("Usage_Time", usageTime);
        } else if (item instanceof Coffre) {
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

        if (inventaireDoc == null) {
            System.out.println("[ajouterItemDansCoffre] Aucun inventaire trouvé pour ce personnage.");
            return false;
        }

        List<Document> items = (List<Document>) inventaireDoc.get("items");

        if (item instanceof Coffre) {
            System.out.println("[ajouterItemDansCoffre] Impossible d'ajouter un coffre dans un autre coffre.");
            return false;
        }

        boolean ajouteDansCoffre = false;
        int usageTimeActuel = RecupererUsageItem(item.getId(), idPersonnage);

        for (int index = 0; index < items.size(); index++) {
            Document doc = items.get(index);
            if (doc != null && "Coffre".equals(doc.getString("type"))) {
                List<Object> contenuCoffre = (List<Object>) doc.get("contenu");

                if (contenuCoffre == null) {
                    System.out.println("[ajouterItemDansCoffre] Contenu coffre invalide.");
                    return false;
                }

                // Debug avant ajout
                System.out.println("[ajouterItemDansCoffre] Taille contenu coffre avant ajout : " + contenuCoffre.size());
                int nbItemsAvant = (int) contenuCoffre.stream().filter(obj -> obj != null).count();
                System.out.println("[ajouterItemDansCoffre] Nombre d'items dans coffre avant ajout : " + nbItemsAvant);

                for (int i = 0; i < contenuCoffre.size(); i++) {
                    if (contenuCoffre.get(i) == null) {
                        Document docItem = toDocumentUsage(item, usageTimeActuel);
                        contenuCoffre.set(i, docItem);
                        doc.put("contenu", contenuCoffre);

                        // Remet à jour l'item coffre dans la liste
                        items.set(index, doc);

                        ajouteDansCoffre = true;
                        System.out.println("[ajouterItemDansCoffre] Item ajouté dans coffre à la position " + i);
                        break;
                    }
                }

                // Debug après ajout
                int nbItemsApres = (int) contenuCoffre.stream().filter(obj -> obj != null).count();
                System.out.println("[ajouterItemDansCoffre] Nombre d'items dans coffre après ajout : " + nbItemsApres);

                if (ajouteDansCoffre) break;
            }
        }

        if (!ajouteDansCoffre) {
            System.out.println("[ajouterItemDansCoffre] Pas de place dans le coffre ou coffre inexistant.");
            return false;
        }

        // Supprimer item dans inventaire principal
        for (int i = 0; i < items.size(); i++) {
            Object obj = items.get(i);
            if (obj instanceof Document docItem) {
                if (item.getId().equals(docItem.getString("_id"))) {
                    items.set(i, null);
                    System.out.println("[ajouterItemDansCoffre] Item supprimé de l'inventaire principal à la position " + i);
                    break;
                }
            }
        }

        Document update = new Document("$set", new Document("items", items));
        System.out.println("[DEBUG] Items complets avant update :");
        for (Document d : items) {
            if (d == null) {
                System.out.println("null slot");
            } else {
                System.out.println(d.toJson());
            }
        }
        collection.updateOne(query, update);

        Document inventaireActualise = collection.find(query).first();
        System.out.println("[DEBUG] Inventaire après update:");
        System.out.println(inventaireActualise.toJson());


        System.out.println("[ajouterItemDansCoffre] Inventaire mis à jour dans MongoDB.");

        return true;
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
