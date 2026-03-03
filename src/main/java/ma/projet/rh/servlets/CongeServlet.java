package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.Conge;
import ma.projet.rh.entities.Employe;
import ma.projet.rh.entities.SoldeConge;
import ma.projet.rh.enums.Role;
import ma.projet.rh.enums.StatutConge;
import ma.projet.rh.enums.TypeConge;
import ma.projet.rh.services.CongeService;
import ma.projet.rh.services.EmployeService;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Servlet pour la gestion des congés
 */
// @WebServlet désactivé - utilisation de web.xml uniquement pour éviter les conflits
// @WebServlet(name = "CongeServlet", urlPatterns = {"/app/conges/*"})
public class CongeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CongeServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private CongeService congeService;

    @EJB
    private EmployeService employeService;
    
    @EJB
    private ma.projet.rh.repositories.UserAccountRepository userAccountRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());

        try {
            switch (action) {
                case "list":
                    listConges(request, response);
                    break;
                case "view":
                    viewConge(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "validation":
                    showValidationList(request, response);
                    break;
                case "solde":
                    showSoldeConges(request, response);
                    break;
                default:
                    listConges(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans CongeServlet (GET)", e);
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
                    createConge(request, response);
                    break;
                case "valider":
                    validerConge(request, response);
                    break;
                case "refuser":
                    refuserConge(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue");
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans CongeServlet (POST)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Liste les congés de l'utilisateur connecté
     */
    private void listConges(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }
        
        if (currentUser.getEmployeId() != null) {
            List<Conge> conges = congeService.findByEmploye(currentUser.getEmployeId());
            request.setAttribute("conges", conges);
            
            // Charger le solde
            congeService.getSoldeActuel(currentUser.getEmployeId()).ifPresent(solde ->
                request.setAttribute("solde", solde)
            );
        }

        request.getRequestDispatcher("/WEB-INF/views/conges/list.jsp").forward(request, response);
    }

    /**
     * Affiche les détails d'un congé
     */
    private void viewConge(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Optional<Conge> congeOpt = congeService.findById(id);

            if (congeOpt.isPresent()) {
                // Vérifier que l'utilisateur a le droit de voir ce congé
                UserSessionDTO currentUser = getCurrentUser(request);
                Conge conge = congeOpt.get();
                
                // L'employé peut voir ses propres congés, les managers/RH peuvent voir les congés à valider
                if (currentUser == null || 
                    (currentUser.getEmployeId() == null || !conge.getEmploye().getId().equals(currentUser.getEmployeId())) &&
                    currentUser.getRole() != Role.MANAGER && currentUser.getRole() != Role.RH && currentUser.getRole() != Role.ADMIN) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
                    return;
                }
                
                request.setAttribute("conge", conge);
                request.getRequestDispatcher("/WEB-INF/views/conges/view.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Congé non trouvé");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    /**
     * Affiche le formulaire de création de demande de congé
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null || currentUser.getEmployeId() == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }
        
        if (currentUser.getEmployeId() != null) {
            // Charger le solde
            Optional<SoldeConge> soldeOpt = congeService.getSoldeActuel(currentUser.getEmployeId());
            soldeOpt.ifPresent(solde -> request.setAttribute("solde", solde));
        }

        request.setAttribute("typesConge", TypeConge.values());
        request.getRequestDispatcher("/WEB-INF/views/conges/create.jsp").forward(request, response);
    }

    /**
     * Crée une nouvelle demande de congé
     */
    private void createConge(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        try {
            if (currentUser.getEmployeId() == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Aucun employé associé");
                return;
            }

            Optional<Employe> employeOpt = employeService.findByIdWithRelations(currentUser.getEmployeId());
            if (!employeOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Employé non trouvé");
                return;
            }

            Conge conge = new Conge();
            conge.setEmploye(employeOpt.get());
            
            // Type de congé
            String typeCongeStr = request.getParameter("typeConge");
            conge.setTypeConge(TypeConge.valueOf(typeCongeStr));
            
            // Dates
            LocalDate dateDebut = LocalDate.parse(request.getParameter("dateDebut"));
            LocalDate dateFin = LocalDate.parse(request.getParameter("dateFin"));
            conge.setDateDebut(dateDebut);
            conge.setDateFin(dateFin);
            
            // Calculer le nombre de jours
            int nombreJours = (int) ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
            conge.setNombreJours(nombreJours);
            
            // Commentaire
            String commentaire = request.getParameter("commentaire");
            conge.setCommentaireEmploye(commentaire);

            // Créer la demande
            Conge savedConge = congeService.creerDemande(conge);

            logger.info("Demande de congé créée: {}", savedConge.getId());
            response.sendRedirect(request.getContextPath() + 
                "/app/conges/view?id=" + savedConge.getId() + "&success=created");

        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation de la demande de congé", e);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("typesConge", TypeConge.values());
            // Recharger le solde
            if (currentUser.getEmployeId() != null) {
                Optional<SoldeConge> soldeOpt = congeService.getSoldeActuel(currentUser.getEmployeId());
                soldeOpt.ifPresent(solde -> request.setAttribute("solde", solde));
            }
            request.getRequestDispatcher("/WEB-INF/views/conges/create.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la demande de congé", e);
            request.setAttribute("error", "Une erreur est survenue : " + e.getMessage());
            request.setAttribute("typesConge", TypeConge.values());
            // Recharger le solde
            if (currentUser != null && currentUser.getEmployeId() != null) {
                Optional<SoldeConge> soldeOpt = congeService.getSoldeActuel(currentUser.getEmployeId());
                soldeOpt.ifPresent(solde -> request.setAttribute("solde", solde));
            }
            request.getRequestDispatcher("/WEB-INF/views/conges/create.jsp").forward(request, response);
        }
    }

    /**
     * Affiche la liste des congés à valider
     */
    private void showValidationList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }
        
        List<Conge> conges;

        if (currentUser.getRole() == Role.RH || currentUser.getRole() == Role.ADMIN) {
            // RH voit les congés validés par le manager
            conges = congeService.findEnAttenteRH();
            request.setAttribute("isRH", true);
        } else if (currentUser.getRole() == Role.MANAGER && currentUser.getEmployeId() != null) {
            // Manager voit les congés de ses subordonnés
            conges = congeService.findEnAttenteManager(currentUser.getEmployeId());
            request.setAttribute("isManager", true);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        request.setAttribute("conges", conges);
        request.getRequestDispatcher("/WEB-INF/views/conges/validation.jsp").forward(request, response);
    }

    /**
     * Valide un congé
     */
    private void validerConge(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        String commentaire = request.getParameter("commentaire");
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            // Récupérer le UserAccount pour la validation
            Optional<ma.projet.rh.entities.UserAccount> userAccountOpt = userAccountRepository.findById(currentUser.getId());
            if (!userAccountOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Utilisateur non trouvé");
                return;
            }
            
            ma.projet.rh.entities.UserAccount userAccount = userAccountOpt.get();
            
            if (currentUser.getRole() == Role.RH || currentUser.getRole() == Role.ADMIN) {
                // Validation RH
                congeService.validerParRH(id, userAccount, commentaire);
                logger.info("Congé validé par RH: {}", id);
            } else if (currentUser.getRole() == Role.MANAGER) {
                // Validation Manager
                congeService.validerParManager(id, userAccount, commentaire);
                logger.info("Congé validé par Manager: {}", id);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            response.sendRedirect(request.getContextPath() + 
                "/app/conges/validation?success=validated");

        } catch (Exception e) {
            logger.error("Erreur lors de la validation du congé", e);
            response.sendRedirect(request.getContextPath() + 
                "/app/conges/validation?error=" + e.getMessage());
        }
    }

    /**
     * Refuse un congé
     */
    private void refuserConge(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        String commentaire = request.getParameter("commentaire");
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            // Récupérer le UserAccount pour le refus
            Optional<ma.projet.rh.entities.UserAccount> userAccountOpt = userAccountRepository.findById(currentUser.getId());
            if (!userAccountOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Utilisateur non trouvé");
                return;
            }
            
            ma.projet.rh.entities.UserAccount userAccount = userAccountOpt.get();
            boolean parManager = (currentUser.getRole() == Role.MANAGER);
            
            congeService.refuser(id, commentaire, parManager, userAccount);

            logger.info("Congé refusé: {}", id);
            response.sendRedirect(request.getContextPath() + 
                "/app/conges/validation?success=refused");

        } catch (Exception e) {
            logger.error("Erreur lors du refus du congé", e);
            response.sendRedirect(request.getContextPath() + 
                "/app/conges/validation?error=" + e.getMessage());
        }
    }

    /**
     * Affiche le solde de congés
     */
    private void showSoldeConges(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null || currentUser.getEmployeId() == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }
        
        if (currentUser.getEmployeId() != null) {
            Optional<SoldeConge> soldeOpt = congeService.getSoldeActuel(currentUser.getEmployeId());
            soldeOpt.ifPresent(solde -> request.setAttribute("solde", solde));
            
            // Historique des congés
            List<Conge> historique = congeService.findByEmploye(currentUser.getEmployeId());
            request.setAttribute("historique", historique);
        }

        request.getRequestDispatcher("/WEB-INF/views/conges/solde.jsp").forward(request, response);
    }

    /**
     * Récupère l'utilisateur connecté
     */
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

    /**
     * Extrait l'action du pathInfo
     */
    private String getActionFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return "list";
        }
        String path = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        if (path.contains("/")) {
            return path.substring(0, path.indexOf("/"));
        }
        return path.isEmpty() ? "list" : path;
    }
}

