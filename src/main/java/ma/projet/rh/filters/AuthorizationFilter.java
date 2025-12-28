package ma.projet.rh.filters;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Filtre d'autorisation basé sur les rôles
 * Contrôle l'accès aux ressources en fonction des rôles des utilisateurs
 */
// @WebFilter désactivé - utilisation de web.xml uniquement
// @WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/app/*"},
//            dispatcherTypes = {DispatcherType.REQUEST})
public class AuthorizationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
    private static final String SESSION_USER_KEY = "currentUser";
    private static final String ACCESS_DENIED_PAGE = "/accessDenied.jsp";

    // Configuration des permissions par URL
    private final Map<String, Set<Role>> urlRoleMapping = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthorizationFilter: INITIALISÉ avec config: {}", filterConfig.getFilterName());
        initializeUrlRoleMapping();
    }

    /**
     * Configure les permissions d'accès par URL
     */
    private void initializeUrlRoleMapping() {
        // Pages Admin uniquement
        addMapping("/app/admin/*", Role.ADMIN);

        // Pages RH uniquement (si existent)
        addMapping("/app/rh/*", Role.ADMIN, Role.RH);

        // Gestion des employés - actions restreintes (Admin et RH seulement)
        addMapping("/app/employes/create", Role.ADMIN, Role.RH);
        addMapping("/app/employes/edit", Role.ADMIN, Role.RH);
        addMapping("/app/employes/delete", Role.ADMIN, Role.RH);
        addMapping("/app/employes/archive", Role.ADMIN, Role.RH);
        
        // Gestion des départements et postes (Admin et RH seulement)
        addMapping("/app/departements/create", Role.ADMIN, Role.RH);
        addMapping("/app/departements/edit", Role.ADMIN, Role.RH);
        addMapping("/app/departements/delete", Role.ADMIN, Role.RH);
        addMapping("/app/postes/create", Role.ADMIN, Role.RH);
        addMapping("/app/postes/edit", Role.ADMIN, Role.RH);
        addMapping("/app/postes/delete", Role.ADMIN, Role.RH);

        // Validation des congés (Manager, RH et Admin)
        addMapping("/app/conges/validation", Role.ADMIN, Role.MANAGER, Role.RH);
        addMapping("/app/conges/valider", Role.ADMIN, Role.MANAGER, Role.RH);
        addMapping("/app/conges/refuser", Role.ADMIN, Role.MANAGER, Role.RH);

        // Gestion des fiches de paie - génération et validation (RH et Admin seulement)
        addMapping("/app/paie/generate", Role.ADMIN, Role.RH);
        addMapping("/app/paie/list", Role.ADMIN, Role.RH);
        addMapping("/app/paie/valider", Role.ADMIN, Role.RH);
        addMapping("/app/paie/payer", Role.ADMIN, Role.RH);

        // Validation des feuilles de temps (Manager, RH et Admin)
        addMapping("/app/temps/validation", Role.ADMIN, Role.MANAGER, Role.RH);
        addMapping("/app/temps/valider", Role.ADMIN, Role.MANAGER, Role.RH);
        addMapping("/app/temps/rejeter", Role.ADMIN, Role.MANAGER, Role.RH);

        // Rapports (RH, Manager et Admin - pas EMPLOYE)
        addMapping("/app/rapports/*", Role.ADMIN, Role.RH, Role.MANAGER);
    }

    /**
     * Ajoute un mapping URL -> Rôles autorisés
     */
    private void addMapping(String urlPattern, Role... roles) {
        Set<Role> roleSet = new HashSet<>();
        for (Role role : roles) {
            roleSet.add(role);
        }
        urlRoleMapping.put(urlPattern, roleSet);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        logger.info("AuthorizationFilter: doFilter appelé pour URI: {}", httpRequest.getRequestURI());
        
        // Récupérer la session
        HttpSession session = httpRequest.getSession(false);
        
        if (session == null) {
            logger.warn("AuthorizationFilter: Aucune session trouvée");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        logger.info("AuthorizationFilter: Session trouvée (ID: {})", session.getId());
        
        // Lister tous les attributs de session
        java.util.Enumeration<String> attrNames = session.getAttributeNames();
        java.util.List<String> attrs = new java.util.ArrayList<>();
        while (attrNames.hasMoreElements()) {
            attrs.add(attrNames.nextElement());
        }
        logger.info("AuthorizationFilter: Attributs de session: {}", attrs);

        // Récupérer l'utilisateur connecté (UserSessionDTO)
        Object userObj = session.getAttribute(SESSION_USER_KEY);
        
        if (userObj == null) {
            logger.error("AuthorizationFilter: Aucun utilisateur dans la session (Session ID: {})", session.getId());
            logger.error("AuthorizationFilter: Attributs disponibles: {}", attrs);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }
        
        logger.info("AuthorizationFilter: Objet trouvé, type: {}", userObj.getClass().getName());
        
        if (!(userObj instanceof ma.projet.rh.dto.UserSessionDTO)) {
            logger.error("AuthorizationFilter: Type incorrect. Attendu: UserSessionDTO, Reçu: {}", userObj.getClass().getName());
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        ma.projet.rh.dto.UserSessionDTO currentUser = (ma.projet.rh.dto.UserSessionDTO) userObj;
        logger.info("AuthorizationFilter: Utilisateur trouvé: {} (Rôle: {})", currentUser.getUsername(), currentUser.getRole());

        // Vérifier si l'utilisateur a les droits d'accès
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Enlever les query strings pour la vérification
        int queryIndex = path.indexOf('?');
        if (queryIndex > 0) {
            path = path.substring(0, queryIndex);
        }

        if (!hasAccess(path, currentUser.getRole())) {
            // Accès refusé
            httpResponse.sendRedirect(httpRequest.getContextPath() + ACCESS_DENIED_PAGE);
            return;
        }

        // Autorisation accordée, continuer
        chain.doFilter(request, response);
    }

    /**
     * Vérifie si un utilisateur avec un rôle donné a accès à une URL
     */
    private boolean hasAccess(String path, Role userRole) {
        logger.debug("AuthorizationFilter: Vérification accès pour path: {}, role: {}", path, userRole);
        
        // Vérifier chaque pattern
        for (Map.Entry<String, Set<Role>> entry : urlRoleMapping.entrySet()) {
            String pattern = entry.getKey();
            Set<Role> allowedRoles = entry.getValue();

            // Convertir le pattern en regex
            String regex = pattern.replace("*", ".*");
            // S'assurer que le pattern correspond au début du path
            regex = "^" + regex + "$";
            
            if (path.matches(regex)) {
                boolean hasAccess = allowedRoles.contains(userRole);
                logger.debug("AuthorizationFilter: Pattern '{}' match, accès: {}", pattern, hasAccess);
                return hasAccess;
            }
        }

        // Par défaut, si aucun mapping restrictif ne correspond, accès autorisé
        // Cela permet aux URLs non listées (comme /app/dashboard, /app/conges/list, etc.) d'être accessibles à tous les rôles authentifiés
        logger.debug("AuthorizationFilter: Aucun pattern restrictif trouvé, accès autorisé par défaut");
        return true;
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}

