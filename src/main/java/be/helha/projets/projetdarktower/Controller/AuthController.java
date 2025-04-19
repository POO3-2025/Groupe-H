package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.User;
import be.helha.projets.projetdarktower.Model.LoginRequest;
import be.helha.projets.projetdarktower.Service.UserService;
import be.helha.projets.projetdarktower.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userService.register(user);
        return "Utilisateur enregistré avec succès !";
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Authentifier l'utilisateur
        if (userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            // Renvoi un message de bienvenue avec le nom de l'utilisateur, pas le token
            return ResponseEntity.ok("Bienvenue " + loginRequest.getUsername() + " !");
        }
        // Si l'authentification échoue, renvoyer une erreur 403
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Échec de la connexion");
    }
}
