package be.helha.projets.projetdarktower.Service;

import be.helha.projets.projetdarktower.Model.User;
import be.helha.projets.projetdarktower.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Méthode d'enregistrement d'un utilisateur (avec mot de passe crypté)
    public void register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // Méthode pour authentifier un utilisateur avec le mot de passe
    public boolean authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            // Si l'utilisateur n'existe pas, retourner false
            return false;
        }
        // Comparer le mot de passe entré avec celui stocké (crypté)
        return encoder.matches(rawPassword, user.getPassword());
    }

    // Vérifier si un utilisateur existe (utile pour la connexion)
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Méthode pour trouver un utilisateur par son nom d'utilisateur
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Vérifier si le mot de passe est correct (pour la connexion)
    public boolean isPasswordCorrect(User user, String rawPassword) {
        return encoder.matches(rawPassword, user.getPassword());
    }
}
