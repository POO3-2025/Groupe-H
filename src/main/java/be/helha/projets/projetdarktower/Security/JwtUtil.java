package be.helha.projets.projetdarktower.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "DarkTowerSecret";

    // Générer un JWT avec le nom d'utilisateur
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // Le nom d'utilisateur comme "subject"
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Heure d'émission
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // Expiration après 1 jour
                .signWith(SignatureAlgorithm.HS512, SECRET)  // Signer avec la clé secrète
                .compact();  // Créer le token
    }

    // Extraire le JWT du header "Authorization"
    public String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // Retirer "Bearer " pour obtenir le token
        }
        return null;
    }

    // Valider le JWT
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token);  // Valider le token
            return true;  // Si aucune exception, le token est valide
        } catch (Exception e) {
            return false;  // Si une exception est levée, le token est invalide
        }
    }

    // Extraire le nom d'utilisateur (subject) du JWT
    public String getUsernameFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();  // Retourne le "subject" qui est le nom d'utilisateur
    }
}
