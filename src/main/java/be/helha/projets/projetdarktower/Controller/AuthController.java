package be.helha.projets.projetdarktower.Controller;


import be.helha.projets.projetdarktower.Model.User;
import be.helha.projets.projetdarktower.Security.JwtUtil;
import be.helha.projets.projetdarktower.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String login(@RequestBody User user) {
        boolean authenticated = userService.authenticate(user.getUsername(), user.getPassword());
        if (authenticated) {
            String token = jwtUtil.generateToken(user.getUsername());
            return "Bonjour " + user.getUsername() + " !\nToken JWT : " + token;
        } else {
            return "Identifiants invalides.";
        }
    }
}

