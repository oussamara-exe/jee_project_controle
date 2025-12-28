package ma.projet.rh.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filtre d'audit pour logger les actions utilisateurs
 */
public class AuditFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Logging basique (peut être étendu avec HistoriqueService)
        String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        // Continuer la chaîne
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}

