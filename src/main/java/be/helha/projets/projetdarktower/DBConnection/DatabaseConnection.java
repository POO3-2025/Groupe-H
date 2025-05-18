package be.helha.projets.projetdarktower.DBConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pour gérer la connexion aux bases de données MySQL et MongoDB.
 *
 * <p>Les informations de configuration sont chargées depuis un fichier JSON nommé {@code config.json}
 * situé dans le classpath.</p>
 *
 * <p>Cette classe permet d'obtenir une connexion JDBC SQL ou un accès à une base MongoDB
 * selon les paramètres spécifiés dans le fichier de configuration.</p>
 */
public class DatabaseConnection {

    /** Instance unique (singleton) de la classe */
    private static DatabaseConnection instance;

    /** Objet JSON contenant la configuration chargée */
    private JsonObject config;

    /**
     * Constructeur privé qui charge la configuration depuis {@code config.json}.
     * Lance une RuntimeException si le fichier est absent ou mal formé.
     */
    private DatabaseConnection() {
        try {
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(
                    DatabaseConnection.class.getClassLoader().getResourceAsStream("config.json"),
                    StandardCharsets.UTF_8
            );
            config = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            System.out.println("Config loaded: " + config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement de config.json !");
        }
    }

    /**
     * Retourne l'instance unique de DatabaseConnection (singleton).
     *
     * @return instance unique de DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Retourne l'objet JSON contenant la configuration complète.
     *
     * @return configuration JSON
     */
    public JsonObject getConfig() {
        return config;
    }

    /**
     * Obtient une connexion JDBC SQL à partir d'une clé de configuration.
     *
     * @param dbKey clé correspondant à la configuration de la base dans config.json
     * @return connexion JDBC à la base SQL
     * @throws SQLException si une erreur survient lors de la création de la connexion ou clé inconnue
     */
    public Connection getSQLConnection(String dbKey) throws SQLException {
        try {
            JsonObject db = config.getAsJsonObject("db");
            JsonObject section = db.getAsJsonObject(dbKey);
            if (section == null) {
                throw new SQLException("Clé de base inconnue dans config.json : " + dbKey);
            }
            JsonObject creds = section.getAsJsonObject("BDCredentials");

            String dbType = creds.get("DBType").getAsString();
            String host = creds.get("HostName").getAsString();
            String port = creds.get("Port").getAsString();
            String dbName = creds.get("DBName").getAsString();
            String user = creds.get("UserName").getAsString();
            String password = creds.get("Password").getAsString();

            String dbUrl = "jdbc:" + dbType + "://" + host + ":" + port + "/" + dbName + "?serverTimezone=UTC";

            return DriverManager.getConnection(dbUrl, user, password);

        } catch (Exception e) {
            throw new SQLException("Erreur lors de la récupération de la connexion SQL : " + e.getMessage());
        }
    }

    /**
     * Obtient une instance MongoDatabase à partir d'une clé de configuration.
     *
     * @param dbKey clé correspondant à la configuration de la base MongoDB dans config.json
     * @return instance de MongoDatabase connectée à la base spécifiée
     * @throws RuntimeException en cas d'erreur de connexion ou clé inconnue
     */
    public MongoDatabase getMongoDatabase(String dbKey) {
        try {
            JsonObject db = config.getAsJsonObject("db");
            JsonObject section = db.getAsJsonObject(dbKey);
            if (section == null) {
                throw new RuntimeException("Clé de base inconnue dans config.json : " + dbKey);
            }

            JsonObject dbConfig = section.getAsJsonObject("BDCredentials");

            String host = dbConfig.get("HostName").getAsString();
            String port = dbConfig.get("Port").getAsString();
            String dbName = dbConfig.get("DBName").getAsString();

            String user = dbConfig.has("UserName") ? dbConfig.get("UserName").getAsString() : "";
            String password = dbConfig.has("Password") ? dbConfig.get("Password").getAsString() : "";

            String uri;

            if (!user.isEmpty() && !password.isEmpty()) {
                uri = "mongodb://" + user + ":" + password + "@" + host + ":" + port;
            } else {
                // Sans authentification
                uri = "mongodb://" + host + ":" + port;
            }

            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase(dbName);

            boolean exists = mongoClient.listDatabaseNames().into(new java.util.ArrayList<>()).contains(dbName);
            if (!exists) {
                System.out.println("La base MongoDB " + dbName + " n'existe pas, elle sera créée à la première insertion.");
            }

            return database;
        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion à MongoDB : " + e.getMessage());
        }
    }
}
