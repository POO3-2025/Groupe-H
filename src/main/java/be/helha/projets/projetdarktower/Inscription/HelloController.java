package be.helha.projets.projetdarktower.Inscription;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class HelloController {
    // Ce point de terminaison est accessible uniquement si l'utilisateur a le rôle "USER"
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/hello")
    public Map<String, String> hello() {
        // Récupérer le nom d'utilisateur à partir du contexte de sécurité
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.toString();
        // Retourner le message avec le nom d'utilisateur
        return Collections.singletonMap("message", "Hello " + username);
    }
}
