package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet pour la gestion des notifications
 */
public class NotificationServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private NotificationService notificationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Récupérer toutes les notifications de l'utilisateur
            request.setAttribute("notifications", 
                notificationService.findByDestinataire(currentUser.getId()));
            
            // Compter les notifications non lues
            long notificationsNonLues = notificationService.countNonLues(currentUser.getId());
            request.setAttribute("notificationsNonLues", notificationsNonLues);

            request.getRequestDispatcher("/WEB-INF/views/notifications/list.jsp")
                .forward(request, response);

        } catch (Exception e) {
            logger.error("Erreur dans NotificationServlet (GET)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        try {
            if ("marquer-lue".equals(action) && idParam != null) {
                Long notificationId = Long.parseLong(idParam);
                notificationService.marquerCommeLue(notificationId);
                logger.info("Notification marquée comme lue: {}", notificationId);
                response.sendRedirect(request.getContextPath() + "/app/notifications?success=read");
            } else if ("marquer-toutes-lues".equals(action)) {
                notificationService.marquerToutesCommeLues(currentUser.getId());
                logger.info("Toutes les notifications marquées comme lues pour l'utilisateur: {}", currentUser.getId());
                response.sendRedirect(request.getContextPath() + "/app/notifications?success=allRead");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue");
            }
        } catch (Exception e) {
            logger.error("Erreur dans NotificationServlet (POST)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private UserSessionDTO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object userObj = session.getAttribute(SESSION_USER_KEY);
        if (userObj instanceof UserSessionDTO) {
            return (UserSessionDTO) userObj;
        }
        return null;
    }
}

