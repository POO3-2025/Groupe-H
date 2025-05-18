package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.User;
import be.helha.projets.projetdarktower.Model.LoginRequest;
import be.helha.projets.projetdarktower.Model.LoginResponse;
import be.helha.projets.projetdarktower.Service.UserService;
import be.helha.projets.projetdarktower.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion de l'authentification utilisateur.
 * Permet l'inscription, la connexion et la mise à jour du statut de connexion.
 */
@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Endpoint pour enregistrer un nouvel utilisateur.
     * Vérifie si le nom d'utilisateur existe déjà avant de créer l'utilisateur.
     *
     * @param user L'objet User envoyé dans le corps de la requête.
     * @return ResponseEntity avec message de succès ou d'erreur (409 si conflit).
     */
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

    /**
     * Endpoint pour authentifier un utilisateur et générer un token JWT.
     * Vérifie l'existence de l'utilisateur et la validité du mot de passe.
     *
     * @param loginRequest Objet contenant username et password pour la connexion.
     * @return ResponseEntity contenant un LoginResponse avec token JWT ou message d'erreur.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Utilisateur inexistant");
        }

        if (!userService.isPasswordCorrect(user, loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        LoginResponse response = new LoginResponse(user.getId(), user.getUsername(), token, user.getIsLoggedIn());
        System.out.println("Réponse brute : " + response);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour mettre à jour le statut de connexion (isLogged) d'un utilisateur.
     *
     * @param userId   L'identifiant de l'utilisateur à mettre à jour.
     * @param isLogged Nouvelle valeur du statut de connexion (ex : 0 ou 1).
     * @return ResponseEntity avec message de succès ou d'erreur.
     */
    @PutMapping("/update-is-logged/{userId}")
    public ResponseEntity<String> updateIsLogged(@PathVariable int userId, @RequestParam int isLogged) {
        try {
            userService.updateIsLogged(userId, isLogged);
            return ResponseEntity.ok("Statut de connexion mis à jour avec succès.");
        } catch (Exception e) {
            e.printStackTrace(); // Affiche dans la console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour du statut de connexion : " + e.toString());
        }
    }
}
