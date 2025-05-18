package be.helha.projets.projetdarktower.Repository;

import be.helha.projets.projetdarktower.Model.User;
import be.helha.projets.projetdarktower.DBConnection.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest {

    private UserRepository userRepository;

    private static final String TEST_DB_KEY = "mysqltest";

    @BeforeAll
    void setupDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getSQLConnection(TEST_DB_KEY);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users")) {
            stmt.executeUpdate();
        }
    }

    @BeforeEach
    void setup() {
        userRepository = new UserRepository("mysqltest");  // utilise la clé de test
    }


    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Sauvegarder un utilisateur et rechercher par username")
    void testSaveAndFindByUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        userRepository.save(user);

        User fetched = userRepository.findByUsername("testuser");
        assertNotNull(fetched);
        assertEquals("testuser", fetched.getUsername());
        assertEquals("password123", fetched.getPassword());
        assertEquals(0, fetched.getIsLoggedIn());
    }

    @Test
    @DisplayName("2: Vérifier existence d'un utilisateur par username")
    void testExistsByUsername() {
        User user = new User();
        user.setUsername("existstest");
        user.setPassword("abc");

        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("existstest"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("3: Mettre à jour le statut isLogged d'un utilisateur")
    void testUpdateIsLogged() {
        User user = new User();
        user.setUsername("loginupdate");
        user.setPassword("pass");

        userRepository.save(user);

        User savedUser = userRepository.findByUsername("loginupdate");
        assertNotNull(savedUser);

        userRepository.updateIsLogged(savedUser.getId().intValue(), 1);

        User updatedUser = userRepository.findByUsername("loginupdate");
        assertEquals(1, updatedUser.getIsLoggedIn());
    }
}
