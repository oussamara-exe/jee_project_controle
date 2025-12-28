package ma.projet.rh.filters;

import ma.projet.rh.dto.UserSessionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtre d'authentification pour protéger les pages sécurisées
 * Vérifie que l'utilisateur est connecté avant d'accéder aux ressources protégées
 */
// @WebFilter désactivé - utilisation de web.xml uniquement
// @WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/app/*"}, 
//            dispatcherTypes = {DispatcherType.REQUEST})
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String LOGIN_PAGE = "/login.jsp";
    private static final String SESSION_USER_KEY = "currentUser";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter: INITIALISÉ avec config: {}", filterConfig.getFilterName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        logger.info("AuthenticationFilter: doFilter appelé pour URI: {}", httpRequest.getRequestURI());

        // Récupérer la session (sans en créer une nouvelle)
        HttpSession session = httpRequest.getSession(false);

        // Vérifier si l'utilisateur est connecté
        boolean isLoggedIn = false;
        if (session != null) {
            Object user = session.getAttribute(SESSION_USER_KEY);
            isLoggedIn = (user != null);
            if (!isLoggedIn) {
                logger.warn("AuthenticationFilter: Session existe mais pas d'utilisateur (Session ID: {})", session.getId());
            } else {
                logger.debug("AuthenticationFilter: Utilisateur authentifié trouvé dans la session (Session ID: {})", session.getId());
            }
        } else {
            logger.debug("AuthenticationFilter: Aucune session trouvée");
        }

        // URI de la requête
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Exclure les pages publiques et ressources statiques
        if (requestURI.equals(contextPath + "/logout") ||
            requestURI.equals(contextPath + "/login.jsp") ||
            requestURI.equals(contextPath + "/") ||
            requestURI.startsWith(contextPath + "/assets/") ||
            requestURI.endsWith(".css") || 
            requestURI.endsWith(".js") ||
            requestURI.endsWith(".png") || 
            requestURI.endsWith(".jpg") ||
            requestURI.endsWith(".gif") ||
            requestURI.endsWith(".ico")) {
            logger.debug("AuthenticationFilter: Page publique ou ressource statique, passage sans authentification: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }
        
        // TOUTES les autres requêtes vers /app/* doivent être authentifiées
        logger.info("AuthenticationFilter: Vérification de l'authentification pour: {}", requestURI);

        // Si l'utilisateur n'est pas connecté, rediriger vers la page de connexion
        if (!isLoggedIn) {
            // Stocker l'URL demandée (relative) pour redirection après connexion
            String requestedURL = requestURI;
            String queryString = httpRequest.getQueryString();
            
            if (queryString != null) {
                requestedURL += "?" + queryString;
            }
            
            session = httpRequest.getSession(true);
            session.setAttribute("requestedURL", requestedURL);

            // Rediriger vers la page de connexion
            httpResponse.sendRedirect(contextPath + LOGIN_PAGE);
            return;
        }

        // Utilisateur connecté, continuer la chaîne de filtres
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}

