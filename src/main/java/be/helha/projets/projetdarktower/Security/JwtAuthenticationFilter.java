package be.helha.projets.projetdarktower.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //  Ignorer les endpoints publics
        String path = request.getServletPath();
        List<String> publicEndpoints = List.of("/login", "/register");
        if (publicEndpoints.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        //  Récupération du token JWT
        String bearer = request.getHeader("Authorization");
        String token = null;

        if (bearer != null && bearer.startsWith("Bearer ")) {
            token = bearer.substring(7);
        }

        //  Si token valide → authentification
        if (token != null && jwtUtil.validateJwtToken(token)) {
            String username = jwtUtil.getUsernameFromJwtToken(token);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
