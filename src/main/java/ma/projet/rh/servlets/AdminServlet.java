package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.ActionHistorique;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.repositories.ActionHistoriqueRepository;
import ma.projet.rh.repositories.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet pour l'administration
 */
public class AdminServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private UserAccountRepository userAccountRepository;

    @EJB
    private ActionHistoriqueRepository actionHistoriqueRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());

        try {
            switch (action) {
                case "utilisateurs":
                    listUtilisateurs(request, response);
                    break;
                case "historique":
                    listHistorique(request, response);
                    break;
                case "parametres":
                    showParametres(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans AdminServlet (GET)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void listUtilisateurs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<UserAccount> utilisateurs = userAccountRepository.findAll();
        request.setAttribute("utilisateurs", utilisateurs);
        request.getRequestDispatcher("/WEB-INF/views/admin/utilisateurs.jsp").forward(request, response);
    }

    private void listHistorique(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ActionHistorique> historique = actionHistoriqueRepository.findAll();
        request.setAttribute("historique", historique);
        request.getRequestDispatcher("/WEB-INF/views/admin/historique.jsp").forward(request, response);
    }

    private void showParametres(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/admin/parametres.jsp").forward(request, response);
    }

    private String getActionFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return "";
        }
        return pathInfo.substring(1);
    }
}

