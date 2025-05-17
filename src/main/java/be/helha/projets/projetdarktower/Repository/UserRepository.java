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
        String sql = "INSERT INTO users (username, password,isLogged) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getSQLConnection(DB_KEY);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, 0);
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
                user.setIsLoggedIn(rs.getInt("isLogged"));
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
    public void updateIsLogged(int userId, int isLogged) {
        String sql = "UPDATE users SET isLogged = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getSQLConnection(DB_KEY);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, isLogged);
            stmt.setLong(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de isLogged : " + e.getMessage());
        }
    }
}
