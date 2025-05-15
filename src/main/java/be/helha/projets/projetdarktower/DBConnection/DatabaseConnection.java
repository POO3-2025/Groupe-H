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
 * Classe singleton pour gérer la connexion aux bases MySQL et MongoDB
 * à partir du fichier JSON config.json
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private JsonObject config;

    private DatabaseConnection() {
        try {
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(
                    DatabaseConnection.class.getClassLoader().getResourceAsStream("config.json"),
                    StandardCharsets.UTF_8
            );
            config = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement de config.json !");
        }
    }

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

    public JsonObject getConfig() {
        return config;
    }

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
