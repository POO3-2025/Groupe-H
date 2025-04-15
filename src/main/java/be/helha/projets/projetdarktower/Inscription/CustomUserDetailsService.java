package be.helha.projets.projetdarktower.Inscription;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Exemple : Récupérer normalement l'utilisateur depuis la base de données
        if (username.equals("admin")) { // Remplacez par une vraie requête
            return User.builder()
                    .username("admin")
                    .password("$2a$12$RWvTjeKK7g/M3nLx/L8Xheh1.4PW1Ekt87FVlHwnv40AJdg80uLt2") // "password" en bcrypt"
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
        }
    }
}
