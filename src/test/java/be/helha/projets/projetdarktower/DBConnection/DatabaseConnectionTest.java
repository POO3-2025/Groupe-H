package be.helha.projets.projetdarktower.DBConnection;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    public void testGetConfigNotNull() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        JsonObject config = instance.getConfig();
        assertNotNull(config, "La config ne doit pas être nulle");
    }

    @Test
    public void testGetSQLConnectionValidKey() throws SQLException {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        // Remplace "mysql_test" par une clé valide dans ton config.json pour MySQL
        String validKey = "mysql_test";
        try (Connection conn = instance.getSQLConnection(validKey)) {
            assertNotNull(conn, "La connexion SQL ne doit pas être nulle");
            assertFalse(conn.isClosed(), "La connexion ne doit pas être fermée");
        }
    }

    @Test
    public void testGetSQLConnectionInvalidKey() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        String invalidKey = "cle_invalide";
        SQLException exception = assertThrows(SQLException.class, () -> {
            instance.getSQLConnection(invalidKey);
        });
        assertTrue(exception.getMessage().contains("Clé de base inconnue"));
    }

    @Test
    public void testGetMongoDatabaseValidKey() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        // Remplace "mongo_test" par une clé valide dans ton config.json pour MongoDB
        String validKey = "mongo_test";

        MongoDatabase db = instance.getMongoDatabase(validKey);
        assertNotNull(db, "La base MongoDB ne doit pas être nulle");
        assertEquals(validKey, db.getName(), "Le nom de la base doit correspondre à la clé utilisée");
    }

    @Test
    public void testGetMongoDatabaseInvalidKey() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        String invalidKey = "cle_invalide";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            instance.getMongoDatabase(invalidKey);
        });
        assertTrue(exception.getMessage().contains("Clé de base inconnue"));
    }
}
