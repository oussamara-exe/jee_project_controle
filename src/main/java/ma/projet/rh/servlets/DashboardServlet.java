package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet pour le tableau de bord principal
 */
// @WebServlet désactivé - utilisation de web.xml uniquement pour éviter les conflits avec les filtres
// @WebServlet(name = "DashboardServlet", urlPatterns = {"/app/dashboard"})
public class DashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private EmployeService employeService;

    @EJB
    private CongeService congeService;

    @EJB
    private NotificationService notificationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("DashboardServlet: doGet appelé pour URI: {}", request.getRequestURI());
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.error("DashboardServlet: Aucune session trouvée, redirection vers login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        logger.info("DashboardServlet: Session trouvée (ID: {})", session.getId());
        
        // Lister tous les attributs de session
        java.util.Enumeration<String> attrNames = session.getAttributeNames();
        java.util.List<String> attrs = new java.util.ArrayList<>();
        while (attrNames.hasMoreElements()) {
            attrs.add(attrNames.nextElement());
        }
        logger.info("DashboardServlet: Attributs de session: {}", attrs);
        
        Object userObj = session.getAttribute(SESSION_USER_KEY);
        if (userObj == null) {
            logger.error("DashboardServlet: Aucun utilisateur dans la session (Session ID: {}), redirection vers login", session.getId());
            logger.error("DashboardServlet: Attributs disponibles: {}", attrs);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        if (!(userObj instanceof UserSessionDTO)) {
            logger.error("DashboardServlet: Type incorrect. Attendu: UserSessionDTO, Reçu: {}", userObj.getClass().getName());
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        UserSessionDTO currentUser = (UserSessionDTO) userObj;
        logger.info("DashboardServlet: Dashboard chargé pour l'utilisateur: {} (Session ID: {})", currentUser.getUsername(), session.getId());

        try {
            // Vérifier que les services sont injectés
            if (employeService == null) {
                logger.error("EmployeService n'est pas injecté !");
                throw new ServletException("EmployeService non disponible");
            }
            if (notificationService == null) {
                logger.error("NotificationService n'est pas injecté !");
                throw new ServletException("NotificationService non disponible");
            }
            
            // Statistiques de base
            long totalEmployes = employeService.countActifs();
            request.setAttribute("totalEmployes", totalEmployes);

            // Notifications non lues
            long notificationsNonLues = notificationService.countNonLues(currentUser.getId());
            request.setAttribute("notificationsNonLues", notificationsNonLues);

            // Statistiques spécifiques au rôle
            if (congeService != null) {
                switch (currentUser.getRole()) {
                    case MANAGER:
                        // Congés en attente de validation
                        if (currentUser.getEmployeId() != null) {
                            long congesEnAttente = congeService.countEnAttenteManager(currentUser.getEmployeId());
                            request.setAttribute("congesEnAttente", congesEnAttente);
                        }
                        break;

                    case RH:
                    case ADMIN:
                        // Congés en attente de validation RH
                        long congesEnAttenteRH = congeService.countEnAttenteRH();
                        request.setAttribute("congesEnAttenteRH", congesEnAttenteRH);
                        break;

                    case EMPLOYE:
                        // Statistiques employé
                        break;
                }
            } else {
                logger.warn("CongeService n'est pas injecté, statistiques congés non disponibles");
            }

            // Notifications récentes
            request.setAttribute("notifications", 
                notificationService.findByDestinataire(currentUser.getId()));

            // Rediriger vers la JSP appropriée selon le rôle
            String dashboardJsp = getDashboardJspForRole(currentUser.getRole().name());
            request.getRequestDispatcher(dashboardJsp).forward(request, response);

        } catch (Exception e) {
            logger.error("Erreur lors du chargement du dashboard", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erreur lors du chargement du dashboard");
        }
    }

    /**
     * Retourne la JSP appropriée selon le rôle
     */
    private String getDashboardJspForRole(String role) {
        switch (role) {
            case "ADMIN":
                return "/WEB-INF/views/dashboard/admin.jsp";
            case "RH":
                return "/WEB-INF/views/dashboard/rh.jsp";
            case "MANAGER":
                return "/WEB-INF/views/dashboard/manager.jsp";
            case "EMPLOYE":
            default:
                return "/WEB-INF/views/dashboard/employe.jsp";
        }
    }
}

