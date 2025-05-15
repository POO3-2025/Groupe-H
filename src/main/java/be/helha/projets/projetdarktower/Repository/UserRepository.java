package be.helha.projets.projetdarktower.Repository;

import be.helha.projets.projetdarktower.Model.User;
import be.helha.projets.projetdarktower.DBConnection.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {

    // Clé config pour la connexion SQL, peut être 'mysqlproduction' ou autre selon config JSON
    private static final String DB_KEY = "mysqlproduction";

    public void save(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getSQLConnection(DB_KEY);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de l'utilisateur : " + e.getMessage());
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getInstance().getSQLConnection(DB_KEY);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur : " + e.getMessage());
        }

        return null;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getInstance().getSQLConnection(DB_KEY);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de l'utilisateur : " + e.getMessage());
        }

        return false;
    }
}
