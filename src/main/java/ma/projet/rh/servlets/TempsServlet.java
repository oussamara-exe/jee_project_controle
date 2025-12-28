package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.Employe;
import ma.projet.rh.entities.FeuilleTemps;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.StatutFeuilleTemps;
import ma.projet.rh.repositories.EmployeRepository;
import ma.projet.rh.repositories.FeuilleTempsRepository;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Servlet pour la gestion des feuilles de temps
 */
public class TempsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TempsServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private ma.projet.rh.services.FeuilleTempsService feuilleTempsService;

    @EJB
    private EmployeRepository employeRepository;

    @EJB
    private UserAccountRepository userAccountRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());

        try {
            switch (action) {
                case "list":
                    listFeuillesTemps(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "validation":
                    showValidationList(request, response);
                    break;
                default:
                    listFeuillesTemps(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans TempsServlet (GET)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());

        try {
            switch (action) {
                case "create":
                    createFeuilleTemps(request, response);
                    break;
                case "soumettre":
                    soumettreFeuilleTemps(request, response);
                    break;
                case "valider":
                    validerFeuilleTemps(request, response);
                    break;
                case "rejeter":
                    rejeterFeuilleTemps(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue");
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans TempsServlet (POST)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void listFeuillesTemps(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        List<FeuilleTemps> feuilles = null;

        if (currentUser != null && currentUser.getEmployeId() != null) {
            feuilles = feuilleTempsService.findByEmploye(currentUser.getEmployeId());
        }

        request.setAttribute("feuilles", feuilles);
        request.getRequestDispatcher("/WEB-INF/views/temps/list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null || currentUser.getEmployeId() == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        // Calculer le lundi de la semaine actuelle
        LocalDate today = LocalDate.now();
        LocalDate lundi = today.with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1);

        request.setAttribute("dateSemaine", lundi);
        request.getRequestDispatcher("/WEB-INF/views/temps/create.jsp").forward(request, response);
    }

    private void showValidationList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null || currentUser.getEmployeId() == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        // Récupérer l'employé (qui est le manager)
        Optional<Employe> managerOpt = employeRepository.findByIdWithRelations(currentUser.getEmployeId());
        if (managerOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employé non trouvé");
            return;
        }

        Employe manager = managerOpt.get();
        // Utiliser le service pour récupérer les feuilles en attente
        List<FeuilleTemps> feuillesEnAttente = feuilleTempsService.findEnAttenteValidation(manager.getId());

        request.setAttribute("feuilles", feuillesEnAttente);
        request.getRequestDispatcher("/WEB-INF/views/temps/validation.jsp").forward(request, response);
    }

    private void createFeuilleTemps(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            UserSessionDTO currentUser = getCurrentUser(request);
            if (currentUser == null || currentUser.getEmployeId() == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
                return;
            }

            Optional<Employe> employeOpt = employeRepository.findByIdWithRelations(currentUser.getEmployeId());
            if (employeOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employé non trouvé");
                return;
            }

            LocalDate dateSemaine = LocalDate.parse(request.getParameter("dateSemaine"));
            BigDecimal heuresNormales = new BigDecimal(request.getParameter("heuresNormales"));

            FeuilleTemps feuille = new FeuilleTemps(employeOpt.get(), dateSemaine);
            feuille.setHeuresNormales(heuresNormales);

            // Utiliser le service pour créer la feuille
            FeuilleTemps saved = feuilleTempsService.creer(feuille);
            logger.info("Feuille de temps créée: {}", saved.getId());

            response.sendRedirect(request.getContextPath() + "/app/temps/list?success=created");

        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation lors de la création de la feuille de temps", e);
            request.setAttribute("error", e.getMessage());
            showCreateForm(request, response);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la feuille de temps", e);
            request.setAttribute("error", "Erreur lors de la création : " + e.getMessage());
            showCreateForm(request, response);
        }
    }

    private void soumettreFeuilleTemps(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            feuilleTempsService.soumettre(id);
            logger.info("Feuille de temps soumise: {}", id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Erreur lors de la soumission", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/app/temps/list?success=submitted");
    }

    private void validerFeuilleTemps(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            String commentaire = request.getParameter("commentaire");
            Optional<UserAccount> validateurOpt = userAccountRepository.findById(currentUser.getId());
            if (validateurOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Validateur non trouvé");
                return;
            }

            feuilleTempsService.valider(id, validateurOpt.get(), commentaire);
            logger.info("Feuille de temps validée: {}", id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Erreur lors de la validation", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/app/temps/validation?success=validated");
    }

    private void rejeterFeuilleTemps(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        String commentaire = request.getParameter("commentaire");
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Optional<UserAccount> validateurOpt = userAccountRepository.findById(currentUser.getId());
            if (validateurOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Validateur non trouvé");
                return;
            }

            feuilleTempsService.rejeter(id, commentaire, validateurOpt.get());
            logger.info("Feuille de temps rejetée: {}", id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Erreur lors du rejet", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/app/temps/validation?success=rejected");
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

    private String getActionFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return "list";
        }
        return pathInfo.substring(1);
    }
}

