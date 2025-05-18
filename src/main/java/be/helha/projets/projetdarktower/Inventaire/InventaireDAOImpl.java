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
        this("MongoDBProduction"); // Par défaut prod
    }

    public InventaireDAOImpl(String dbKey) {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        MongoDatabase database;
        try {
            database = dbConn.getMongoDatabase(dbKey);
        } catch (RuntimeException e) {
            throw new RuntimeException("Impossible de se connecter à MongoDB: " + e.getMessage());
        }
        this.collection = database.getCollection("Inventaire");
    }


    public boolean hasCoffreInInventory(int idPersonnage) {
        Document query = new Document("idPersonnage", idPersonnage);
        Document userInventory = collection.find(query).first();

        if (userInventory != null) {
            Object obj = userInventory.get("items");
            if (obj instanceof List<?>) {
                List<?> rawList = (List<?>) obj;
                List<Document> items = new ArrayList<>();
                for (Object o : rawList) {
                    if (o instanceof Document) {
                        items.add((Document) o);
                    }
                }
                for (Document item : items) {
                    if (item != null && "Coffre".equals(item.getString("type"))) {
                        return true;
                    }
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
            return false;
        }

        Object objItems = inventaireDoc.get("items");
        if (!(objItems instanceof List<?>)) {
            return false;
        }

        List<?> rawItems = (List<?>) objItems;
        List<Object> items = new ArrayList<>(rawItems.size());
        for (Object o : rawItems) {
            items.add(o);
        }

        // Vérifie qu’il n’y ait pas déjà un coffre
        if (item instanceof Coffre) {
            for (Object obj : items) {
                if (obj instanceof Document) {
                    Document doc = (Document) obj;
                    if ("Coffre".equals(doc.getString("type"))) {
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


        Document inventoryDoc = collection.find(new Document("idPersonnage", idPersonnage)).first();

        if (inventoryDoc != null) {
            Object obj = inventoryDoc.get("items");

            if (obj instanceof List<?>) {
                List<?> rawList = (List<?>) obj;

                // On essaie de convertir en List<Document> en filtrant ou supposant que c'est bien le cas
                List<Document> items = new ArrayList<>();
                for (Object o : rawList) {
                    if (o instanceof Document) {
                        items.add((Document) o);
                    }
                }

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
                        Object contenuObj = doc.get("contenu");
                        if (contenuObj instanceof List<?>) {
                            List<?> rawContenu = (List<?>) contenuObj;
                            List<Document> contenu = new ArrayList<>();
                            for (Object o : rawContenu) {
                                if (o instanceof Document) {
                                    contenu.add((Document) o);
                                }
                            }

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
        }

        return null;
    }


    public String DeleteItem(String itemId) {
        // Parcourt chaque inventaire
        FindIterable<Document> allInventories = collection.find();

        for (Document inventory : allInventories) {
            Object objItems = inventory.get("items");

            if (!(objItems instanceof List<?>)) {
                continue; // passe au suivant si ce n'est pas une liste
            }

            List<?> rawItems = (List<?>) objItems;
            List<Object> items = new ArrayList<>();
            for (Object o : rawItems) {
                items.add(o);
            }

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
                        Object objContenu = itemDoc.get("contenu");

                        if (objContenu instanceof List<?>) {
                            List<?> rawContenu = (List<?>) objContenu;
                            List<Object> contenu = new ArrayList<>();
                            for (Object oCont : rawContenu) {
                                contenu.add(oCont);
                            }

                            for (int j = 0; j < contenu.size(); j++) {
                                Object objContenuItem = contenu.get(j);
                                if (objContenuItem instanceof Document) {
                                    Document docContenu = (Document) objContenuItem;
                                    if (itemId.equals(docContenu.getString("_id"))) {
                                        // Supprime item dans coffre
                                        contenu.set(j, null);
                                        // Met à jour la liste "contenu" dans l'objet itemDoc
                                        itemDoc.put("contenu", contenu);
                                        updated = true;
                                        break;
                                    }
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

        Object objItems = inventoryDoc.get("items");
        if (!(objItems instanceof List<?>)) {
            System.out.println("Format d'inventaire incorrect pour idPersonnage = " + idPersonnage);
            return;
        }

        List<?> rawItems = (List<?>) objItems;
        List<Document> items = new ArrayList<>();
        for (Object o : rawItems) {
            if (o instanceof Document) {
                items.add((Document) o);
            } else {
                items.add(null); // ou gérer autrement si tu préfères
            }
        }

        boolean updated = false;

        for (Document doc : items) {
            if (doc != null && itemId.equals(doc.getString("_id"))) {
                int usage = doc.getInteger("Usage_Time", 0);
                if (usage > 0) {
                    doc.put("Usage_Time", usage - 1);
                    updated = true;
                }
                break;
            }
            if (doc != null && "Coffre".equals(doc.getString("type"))) {
                Object objContenu = doc.get("contenu");
                if (!(objContenu instanceof List<?>)) {
                    continue;
                }
                List<?> rawContenu = (List<?>) objContenu;
                List<Document> contenu = new ArrayList<>();
                for (Object o : rawContenu) {
                    if (o instanceof Document) {
                        contenu.add((Document) o);
                    } else {
                        contenu.add(null);
                    }
                }

                for (Document itemDoc : contenu) {
                    if (itemDoc != null && itemId.equals(itemDoc.getString("_id"))) {
                        int usage = itemDoc.getInteger("Usage_Time", 0);
                        if (usage > 0) {
                            itemDoc.put("Usage_Time", usage - 1);
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
        Document query = new Document("idPersonnage", idPersonnage);
        Document inventory = collection.find(query).first();

        if (inventory != null) {
            Object objItems = inventory.get("items");
            if (!(objItems instanceof List<?>)) {

                return 0;
            }

            List<?> rawItems = (List<?>) objItems;
            List<Document> items = new ArrayList<>();
            for (Object o : rawItems) {
                if (o instanceof Document) {
                    items.add((Document) o);
                } else {
                    items.add(null);  // ou gérer différemment selon besoin
                }
            }

            // Chercher dans inventaire principal
            for (Document item : items) {
                if (item != null && itemId.equals(item.getString("_id"))) {
                    return item.getInteger("Usage_Time", 0);
                }
            }

            // Chercher dans contenu des coffres
            for (Document item : items) {
                if (item != null && "Coffre".equals(item.getString("type"))) {
                    Object objContenu = item.get("contenu");
                    if (!(objContenu instanceof List<?>)) {
                        continue;
                    }
                    List<?> rawContenu = (List<?>) objContenu;
                    List<Document> contenu = new ArrayList<>();
                    for (Object o : rawContenu) {
                        if (o instanceof Document) {
                            contenu.add((Document) o);
                        } else {
                            contenu.add(null);
                        }
                    }

                    for (Document itemDoc : contenu) {
                        if (itemDoc != null && itemId.equals(itemDoc.getString("_id"))) {
                            return itemDoc.getInteger("Usage_Time", 0);
                        }
                    }
                }
            }
        }
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

        Object objItems = inventaireDoc.get("items");
        if (!(objItems instanceof List<?>)) {
            return new ArrayList<>();
        }
        List<?> rawList = (List<?>) objItems;

        List<Document> items = new ArrayList<>();
        for (Object o : rawList) {
            if (o instanceof Document) {
                items.add((Document) o);
            }
        }

        for (Document item : items) {
            if (item != null && "Coffre".equals(item.getString("type"))) {
                List<Item> contenu = new ArrayList<>();
                Object rawContenuObj = item.get("contenu");
                if (!(rawContenuObj instanceof List<?>)) {
                    continue;
                }
                List<?> rawContenu = (List<?>) rawContenuObj;

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

                    return false;
                }

                // Debug avant ajout

                int nbItemsAvant = (int) contenuCoffre.stream().filter(obj -> obj != null).count();


                for (int i = 0; i < contenuCoffre.size(); i++) {
                    if (contenuCoffre.get(i) == null) {
                        Document docItem = toDocumentUsage(item, usageTimeActuel);
                        contenuCoffre.set(i, docItem);
                        doc.put("contenu", contenuCoffre);

                        // Remet à jour l'item coffre dans la liste
                        items.set(index, doc);

                        ajouteDansCoffre = true;
                        break;
                    }
                }

                // Debug après ajout
                int nbItemsApres = (int) contenuCoffre.stream().filter(obj -> obj != null).count();

                if (ajouteDansCoffre) break;
            }
        }

        if (!ajouteDansCoffre) {

            return false;
        }

        // Supprimer item dans inventaire principal
        for (int i = 0; i < items.size(); i++) {
            Object obj = items.get(i);
            if (obj instanceof Document docItem) {
                if (item.getId().equals(docItem.getString("_id"))) {
                    items.set(i, null);
                    break;
                }
            }
        }

        Document update = new Document("$set", new Document("items", items));
        for (Document d : items) {
            if (d == null) {
                System.out.println("null slot");
            } else {
                System.out.println(d.toJson());
            }
        }
        collection.updateOne(query, update);


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
