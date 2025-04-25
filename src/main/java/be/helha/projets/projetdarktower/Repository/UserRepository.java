package be.helha.projets.projetdarktower.Repository;

import be.helha.projets.projetdarktower.Model.User;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

@Repository
public class UserRepository {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            // Charger le fichier config.json
            FileReader reader = new FileReader("src/main/resources/static/config.json");
            StringBuilder jsonContent = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                jsonContent.append((char) i);
            }
            reader.close();

            // Analyser le JSON
            JSONObject config = new JSONObject(jsonContent.toString());
            JSONObject dbConfig = config.getJSONObject("db");

            // Récupérer les informations de connexion à la base de données
            URL = dbConfig.getString("url");
            USER = dbConfig.getString("username");
            PASSWORD = dbConfig.getString("password");

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier config.json : " + e.getMessage());
        }
    }

    public void save(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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
