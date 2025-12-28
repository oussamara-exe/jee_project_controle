package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.repositories.UserAccountRepository;
import ma.projet.rh.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet de déconnexion
 */
// @WebServlet désactivé - utilisation de web.xml uniquement
// @WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private AuthenticationService authenticationService;

    @EJB
    private UserAccountRepository userAccountRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("Tentative de déconnexion");

        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Récupérer UserSessionDTO de la session
            Object sessionUser = session.getAttribute(SESSION_USER_KEY);
            
            if (sessionUser instanceof UserSessionDTO) {
                UserSessionDTO userDTO = (UserSessionDTO) sessionUser;
                
                // Récupérer le UserAccount complet depuis la base pour l'historique
                Optional<UserAccount> userOpt = userAccountRepository.findById(userDTO.getId());
                
                if (userOpt.isPresent()) {
                    UserAccount user = userOpt.get();
                    String ipAddress = request.getRemoteAddr();
                    
                    try {
                        authenticationService.logout(user, ipAddress);
                        logger.info("Déconnexion de l'utilisateur: {}", user.getUsername());
                    } catch (Exception e) {
                        logger.error("Erreur lors de l'enregistrement de la déconnexion: {}", e.getMessage(), e);
                        // Continuer quand même la déconnexion
                    }
                } else {
                    logger.warn("Utilisateur non trouvé dans la base pour l'ID: {}", userDTO.getId());
                }
            } else if (sessionUser != null) {
                logger.warn("Type d'objet inattendu dans la session: {}", sessionUser.getClass().getName());
            }
            
            // Invalider la session
            try {
                session.invalidate();
                logger.info("Session invalidée avec succès");
            } catch (Exception e) {
                logger.error("Erreur lors de l'invalidation de la session: {}", e.getMessage(), e);
            }
        } else {
            logger.info("Aucune session active à invalider");
        }

        // Rediriger vers la page de connexion
        String contextPath = request.getContextPath();
        logger.info("Redirection vers la page de connexion: {}", contextPath + "/login.jsp");
        response.sendRedirect(contextPath + "/login.jsp");
    }
}

