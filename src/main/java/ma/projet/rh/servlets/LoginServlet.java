package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.services.AuthenticationService;
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
import java.util.Optional;

/**
 * Servlet de gestion de la connexion
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private AuthenticationService authenticationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        logger.info("LoginServlet: doGet appelé pour URI: {}", request.getRequestURI());
        
        // Si déjà connecté, rediriger vers le dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SESSION_USER_KEY) != null) {
            logger.info("LoginServlet: Utilisateur déjà connecté, redirection vers dashboard");
            response.sendRedirect(request.getContextPath() + "/app/dashboard");
            return;
        }

        // Afficher la page de connexion
        logger.info("LoginServlet: Affichage de la page de connexion");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ipAddress = request.getRemoteAddr();

        // Validation des champs
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Veuillez saisir un nom d'utilisateur et un mot de passe");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Tentative d'authentification
        Optional<UserAccount> userOpt = authenticationService.authenticate(
            username.trim(), 
            password, 
            ipAddress
        );

        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            
            // Créer un DTO léger pour la session (évite les problèmes de sérialisation avec les entités JPA)
            UserSessionDTO userSession = new UserSessionDTO(
                user.getId(),
                user.getUsername(),
                user.getRole()
            );
            
            // Ajouter les informations de l'employé si disponible
            if (user.getEmploye() != null) {
                userSession.setEmployeId(user.getEmploye().getId());
                userSession.setEmployeNom(user.getEmploye().getNom());
                userSession.setEmployePrenom(user.getEmploye().getPrenom());
            }
            
            // Récupérer la session actuelle (ou en créer une nouvelle)
            HttpSession session = request.getSession(true);
            
            // Récupérer l'URL demandée depuis la session si elle existe
            String requestedURL = (String) session.getAttribute("requestedURL");
            
            // Stocker l'utilisateur dans la session
            session.setAttribute(SESSION_USER_KEY, userSession);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            logger.info("Connexion réussie pour l'utilisateur: {} (Session ID: {})", username, session.getId());
            logger.info("Utilisateur stocké dans la session: {}", userSession.getUsername());
            
            // Vérifier que l'utilisateur est bien dans la session
            Object verifyUser = session.getAttribute(SESSION_USER_KEY);
            if (verifyUser == null) {
                logger.error("ERREUR: L'utilisateur n'a pas pu être stocké dans la session !");
            } else {
                logger.info("Vérification: Utilisateur confirmé dans la session: {}", ((UserSessionDTO) verifyUser).getUsername());
            }

            // Rediriger vers l'URL demandée ou le dashboard
            String redirectURL;
            if (requestedURL != null && !requestedURL.isEmpty()) {
                redirectURL = requestedURL;
            } else {
                redirectURL = request.getContextPath() + "/app/dashboard";
            }
            
            logger.info("Redirection vers: {}", redirectURL);
            
            // Vérifier si l'URL est déjà absolue (commence par http:// ou https://)
            String finalURL;
            if (redirectURL.startsWith("http://") || redirectURL.startsWith("https://")) {
                // URL déjà absolue, l'utiliser telle quelle
                finalURL = redirectURL;
            } else {
                // URL relative, construire l'URL absolue
                finalURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + redirectURL;
            }
            
            logger.info("Redirection finale vers: {}", finalURL);
            response.sendRedirect(finalURL);
        } else {
            // Authentification échouée
            logger.warn("Échec de connexion pour l'utilisateur: {}", username);
            request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}

