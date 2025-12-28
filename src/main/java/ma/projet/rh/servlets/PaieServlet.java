package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.Employe;
import ma.projet.rh.entities.FichePaie;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.StatutFichePaie;
import ma.projet.rh.repositories.EmployeRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servlet pour la gestion de la paie
 */
public class PaieServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PaieServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private ma.projet.rh.services.FichePaieService fichePaieService;

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
                    listFichesPaie(request, response);
                    break;
                case "mes-fiches":
                    mesFichesPaie(request, response);
                    break;
                case "view":
                    viewFichePaie(request, response);
                    break;
                case "generate":
                    showGenerateForm(request, response);
                    break;
                default:
                    listFichesPaie(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans PaieServlet (GET)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());

        try {
            switch (action) {
                case "generate":
                    generateFichesPaie(request, response);
                    break;
                case "valider":
                    validerFichePaie(request, response);
                    break;
                case "payer":
                    marquerCommePaye(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue");
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans PaieServlet (POST)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void listFichesPaie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<FichePaie> fiches = fichePaieService.findAll();
        request.setAttribute("fiches", fiches);
        request.getRequestDispatcher("/WEB-INF/views/paie/list.jsp").forward(request, response);
    }

    private void mesFichesPaie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        List<FichePaie> fiches = null;

        if (currentUser != null && currentUser.getEmployeId() != null) {
            fiches = fichePaieService.findByEmploye(currentUser.getEmployeId());
        }

        request.setAttribute("fiches", fiches);
        request.getRequestDispatcher("/WEB-INF/views/paie/mes-fiches.jsp").forward(request, response);
    }

    private void viewFichePaie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Optional<FichePaie> ficheOpt = fichePaieService.findByIdWithRelations(id);

            if (ficheOpt.isPresent()) {
                request.setAttribute("fiche", ficheOpt.get());
                request.getRequestDispatcher("/WEB-INF/views/paie/view.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fiche de paie non trouvée");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void showGenerateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Employe> employes = employeRepository.findActifs(0, 1000);
        request.setAttribute("employes", employes);
        
        // Mois et année par défaut (mois précédent)
        LocalDate now = LocalDate.now();
        int mois = now.getMonthValue() - 1;
        int annee = now.getYear();
        if (mois == 0) {
            mois = 12;
            annee--;
        }
        
        request.setAttribute("mois", mois);
        request.setAttribute("annee", annee);
        request.getRequestDispatcher("/WEB-INF/views/paie/generate.jsp").forward(request, response);
    }

    private void generateFichesPaie(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            int mois = Integer.parseInt(request.getParameter("mois"));
            int annee = Integer.parseInt(request.getParameter("annee"));
            String employeIdStr = request.getParameter("employeId");

            int count;
            if (employeIdStr != null && !employeIdStr.isEmpty()) {
                // Générer pour un seul employé
                Long employeId = Long.parseLong(employeIdStr);
                fichePaieService.genererFichePaie(employeId, mois, annee);
                count = 1;
            } else {
                // Générer pour tous les employés actifs
                count = fichePaieService.genererFichesPourMois(mois, annee);
            }

            logger.info("{} fiches de paie générées pour {}/{}", count, mois, annee);
            response.sendRedirect(request.getContextPath() + "/app/paie/list?success=generated&count=" + count);

        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation lors de la génération", e);
            request.setAttribute("error", e.getMessage());
            showGenerateForm(request, response);
        } catch (Exception e) {
            logger.error("Erreur lors de la génération des fiches de paie", e);
            request.setAttribute("error", "Erreur lors de la génération : " + e.getMessage());
            showGenerateForm(request, response);
        }
    }

    private void validerFichePaie(HttpServletRequest request, HttpServletResponse response)
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
            Optional<UserAccount> validateurOpt = userAccountRepository.findById(currentUser.getId());
            if (validateurOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Validateur non trouvé");
                return;
            }

            fichePaieService.valider(id, validateurOpt.get());
            logger.info("Fiche de paie validée: {}", id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Erreur lors de la validation", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/app/paie/list?success=validated");
    }

    private void marquerCommePaye(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            fichePaieService.marquerCommePaye(id);
            logger.info("Fiche de paie marquée comme payée: {}", id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Erreur lors du marquage comme payé", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/app/paie/list?success=paid");
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

