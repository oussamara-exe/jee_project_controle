package ma.projet.rh.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/**
 * Filtre de protection CSRF
 */
public class CsrfFilter implements Filter {

    private static final String CSRF_TOKEN_NAME = "csrfToken";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);
        
        // Générer un token CSRF si absent
        if (session.getAttribute(CSRF_TOKEN_NAME) == null) {
            session.setAttribute(CSRF_TOKEN_NAME, UUID.randomUUID().toString());
        }
        
        // Pour les requêtes POST, vérifier le token (simplifié ici)
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            // Vérification basique (peut être améliorée)
            // Pour l'instant, on laisse passer toutes les requêtes
        }
        
        // Ajouter le token comme attribut de requête
        request.setAttribute(CSRF_TOKEN_NAME, session.getAttribute(CSRF_TOKEN_NAME));
        
        // Continuer la chaîne
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}

