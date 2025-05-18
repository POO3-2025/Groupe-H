package be.helha.projets.projetdarktower.DBConnection;

import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseConnectionTest {

    private DatabaseConnection dbConnection;

    @BeforeAll
    void init() {
        dbConnection = DatabaseConnection.getInstance();
        assertNotNull(dbConnection, "Instance DatabaseConnection ne doit pas être nulle");
    }

    @Test
    @DisplayName("1: Chargement de la configuration JSON")
    void testConfigLoading() {
        assertNotNull(dbConnection.getConfig(), "La configuration JSON doit être chargée");
        assertTrue(dbConnection.getConfig().has("db"), "La configuration doit contenir la clé 'db'");
    }

    @Test
    @DisplayName("2: Obtenir une connexion SQL valide")
    void testGetSQLConnection() throws SQLException {
        // Essayer la connexion SQL test (mysqltest)
        Connection conn = dbConnection.getSQLConnection("mysqltest");
        assertNotNull(conn, "La connexion SQL ne doit pas être nulle");
        assertFalse(conn.isClosed(), "La connexion SQL doit être ouverte");
        conn.close();
    }

    @Test
    @DisplayName("3: Obtenir une connexion SQL avec clé inconnue doit lever une exception")
    void testGetSQLConnectionInvalidKey() {
        SQLException exception = assertThrows(SQLException.class, () -> {
            dbConnection.getSQLConnection("cle_inconnue");
        });
        assertTrue(exception.getMessage().contains("Clé de base inconnue"));
    }

    @Test
    @DisplayName("4: Obtenir une instance MongoDatabase valide")
    void testGetMongoDatabase() {
        MongoDatabase mongoDatabase = dbConnection.getMongoDatabase("MongoDBProduction");
        assertNotNull(mongoDatabase, "L'instance MongoDatabase ne doit pas être nulle");
        assertEquals("Game", mongoDatabase.getName(), "Nom de la base MongoDB doit être 'Game'");
    }

    @Test
    @DisplayName("5: Obtenir MongoDatabase avec clé inconnue doit lever RuntimeException")
    void testGetMongoDatabaseInvalidKey() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dbConnection.getMongoDatabase("cle_inconnue");
        });
        assertTrue(exception.getMessage().contains("Clé de base inconnue"));
    }
}
