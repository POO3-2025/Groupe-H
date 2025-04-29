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

    public void register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("Utilisateur introuvable.");
            return false;
        }
        boolean match = encoder.matches(rawPassword, user.getPassword());
        System.out.println("Password match: " + match);
        return match;
    }
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

}
