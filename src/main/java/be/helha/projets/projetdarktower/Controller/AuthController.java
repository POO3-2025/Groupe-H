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
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userService.userExists(user.getUsername())) {
            // Renvoie une réponse HTTP 409 (conflit) si l'utilisateur existe
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nom d'utilisateur existe déjà");
        }

        userService.register(user);
        return ResponseEntity.ok("Utilisateur enregistré avec succès !");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Vérification si l'utilisateur existe
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Utilisateur inexistant");
        }

        // Vérification si le mot de passe est correct
        if (!userService.isPasswordCorrect(user, loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Mot de passe incorrect . Veuillez saisir le bon mot de passe !");
        }

        // Génération du token JWT si tout est ok
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        return ResponseEntity.ok("Bienvenue " + loginRequest.getUsername() + " !");
    }
}
