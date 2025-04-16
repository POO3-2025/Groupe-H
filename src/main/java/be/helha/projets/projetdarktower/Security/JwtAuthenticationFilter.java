package be.helha.projets.projetdarktower.Security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.stereotype.Component; // Assure-toi d'importer cette annotation

@Component  // Cette annotation permet à Spring de gérer JwtAuthenticationFilter comme un bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtils;

    public JwtAuthenticationFilter(JwtUtil jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Extraire le JWT de la requête
        String jwtToken = jwtUtils.extractJwtFromRequest(request);

        if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken)) {
            // Extraire le nom d'utilisateur
            String username = jwtUtils.getUsernameFromJwtToken(jwtToken);

            // Si l'utilisateur est trouvé, créer un objet d'authentification
            if (username != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, null);  // Pas de rôles ici, mais tu pourrais en ajouter si nécessaire
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Enregistrer l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Passer la requête au prochain filtre
        chain.doFilter(request, response);
    }
}
