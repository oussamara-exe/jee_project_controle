package ma.projet.rh.servlets;

import ma.projet.rh.entities.Departement;
import ma.projet.rh.entities.Employe;
import ma.projet.rh.entities.Poste;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.Role;
import ma.projet.rh.repositories.DepartementRepository;
import ma.projet.rh.repositories.PosteRepository;
import ma.projet.rh.services.CongeService;
import ma.projet.rh.services.EmployeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Servlet pour la gestion des employés (CRUD)
 */
// @WebServlet désactivé - utilisation de web.xml uniquement pour éviter les conflits
// @WebServlet(name = "EmployeServlet", urlPatterns = {"/app/employes/*"})
@MultipartConfig
public class EmployeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EmployeServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private EmployeService employeService;

    @EJB
    private DepartementRepository departementRepository;

    @EJB
    private PosteRepository posteRepository;

    @EJB
    private CongeService congeService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());

        try {
            switch (action) {
                case "list":
                    listEmployes(request, response);
                    break;
                case "view":
                    viewEmploye(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "search":
                    searchEmployes(request, response);
                    break;
                default:
                    listEmployes(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans EmployeServlet (GET)", e);
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
                    createEmploye(request, response);
                    break;
                case "edit":
                    updateEmploye(request, response);
                    break;
                case "delete":
                    deleteEmploye(request, response);
                    break;
                case "archive":
                    archiveEmploye(request, response);
                    break;
                case "restore":
                    restoreEmploye(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue");
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans EmployeServlet (POST)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Liste tous les employés
     */
    private void listEmployes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pageParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");

        int page = 0;
        int pageSize = 20;
        
        try {
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
            if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
                pageSize = Integer.parseInt(pageSizeParam);
            }
        } catch (NumberFormatException e) {
            // Utiliser les valeurs par défaut
        }

        List<Employe> employes = employeService.findActifs(page, pageSize);
        long totalEmployes = employeService.countActifs();
        int totalPages = (int) Math.ceil((double) totalEmployes / pageSize);

        request.setAttribute("employes", employes);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/employes/list.jsp").forward(request, response);
    }

    /**
     * Affiche les détails d'un employé
     */
    private void viewEmploye(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Optional<Employe> employeOpt = employeService.findByIdWithRelations(id);

        if (employeOpt.isPresent()) {
            request.setAttribute("employe", employeOpt.get());
            
            // Charger le solde de congés
            congeService.getSoldeActuel(id).ifPresent(solde -> 
                request.setAttribute("soldeConge", solde)
            );
            
            request.getRequestDispatcher("/WEB-INF/views/employes/view.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employé non trouvé");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    /**
     * Affiche le formulaire de création
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        loadFormData(request);
        request.getRequestDispatcher("/WEB-INF/views/employes/create.jsp").forward(request, response);
    }

    /**
     * Affiche le formulaire d'édition
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Optional<Employe> employeOpt = employeService.findByIdWithRelations(id);

        if (employeOpt.isPresent()) {
            request.setAttribute("employe", employeOpt.get());
            loadFormData(request);
            request.getRequestDispatcher("/WEB-INF/views/employes/edit.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employé non trouvé");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    /**
     * Crée un nouvel employé
     */
    private void createEmploye(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Employe employe = buildEmployeFromRequest(request);
            
            // Informations du compte utilisateur
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String roleStr = request.getParameter("role");
            Role role = roleStr != null ? Role.valueOf(roleStr) : Role.EMPLOYE;

            // Créer l'employé avec son compte
            Employe savedEmploye = employeService.create(employe, username, password, role);

            // Initialiser le solde de congés pour l'année en cours
            congeService.initialiserSoldeConge(savedEmploye, Year.now().getValue());

            logger.info("Employé créé avec succès: {}", savedEmploye.getId());

            // Rediriger vers la liste
            response.sendRedirect(request.getContextPath() + "/app/employes/list?success=created");

        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation lors de la création de l'employé", e);
            request.setAttribute("error", e.getMessage());
            loadFormData(request);
            request.getRequestDispatcher("/WEB-INF/views/employes/create.jsp").forward(request, response);
        }
    }

    /**
     * Met à jour un employé existant
     */
    private void updateEmploye(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
                return;
            }
            Long id = Long.parseLong(idParam);
            Optional<Employe> existingOpt = employeService.findByIdWithRelations(id);

            if (!existingOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employé non trouvé");
                return;
            }

            Employe employe = buildEmployeFromRequest(request);
            employe.setId(id);
            employe.setVersion(existingOpt.get().getVersion()); // Optimistic locking

            employeService.update(employe);

            logger.info("Employé mis à jour avec succès: {}", id);

            response.sendRedirect(request.getContextPath() + "/app/employes/view?id=" + id + "&success=updated");

        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation lors de la mise à jour de l'employé", e);
            request.setAttribute("error", e.getMessage());
            loadFormData(request);
            request.getRequestDispatcher("/WEB-INF/views/employes/edit.jsp").forward(request, response);
        }
    }

    /**
     * Archive un employé
     */
    private void archiveEmploye(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            employeService.archive(id);

            logger.info("Employé archivé: {}", id);
            response.sendRedirect(request.getContextPath() + "/app/employes/list?success=archived");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    /**
     * Restaure un employé archivé
     */
    private void restoreEmploye(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            employeService.restore(id);

            logger.info("Employé restauré: {}", id);
            response.sendRedirect(request.getContextPath() + "/app/employes/view?id=" + id + "&success=restored");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    /**
     * Supprime définitivement un employé
     */
    private void deleteEmploye(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            employeService.delete(id);

            logger.warn("Employé supprimé définitivement: {}", id);
            response.sendRedirect(request.getContextPath() + "/app/employes/list?success=deleted");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    /**
     * Recherche d'employés
     */
    private void searchEmployes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = request.getParameter("q");
        List<Employe> employes = employeService.search(searchTerm);

        request.setAttribute("employes", employes);
        request.setAttribute("searchTerm", searchTerm);

        request.getRequestDispatcher("/WEB-INF/views/employes/list.jsp").forward(request, response);
    }

    /**
     * Construit un objet Employe depuis les paramètres de la requête
     */
    private Employe buildEmployeFromRequest(HttpServletRequest request) {
        Employe employe = new Employe();

        employe.setMatricule(request.getParameter("matricule"));
        employe.setNom(request.getParameter("nom"));
        employe.setPrenom(request.getParameter("prenom"));
        employe.setEmail(request.getParameter("email"));
        employe.setTelephone(request.getParameter("telephone"));
        employe.setAdresse(request.getParameter("adresse"));
        employe.setIban(request.getParameter("iban"));

        // Dates
        String dateNaissance = request.getParameter("dateNaissance");
        if (dateNaissance != null && !dateNaissance.isEmpty()) {
            employe.setDateNaissance(LocalDate.parse(dateNaissance));
        }

        String dateEmbauche = request.getParameter("dateEmbauche");
        if (dateEmbauche != null && !dateEmbauche.isEmpty()) {
            employe.setDateEmbauche(LocalDate.parse(dateEmbauche));
        }

        // Salaire
        String salaireBase = request.getParameter("salaireBase");
        if (salaireBase != null && !salaireBase.isEmpty()) {
            employe.setSalaireBase(new BigDecimal(salaireBase));
        }

        // Heures hebdomadaires
        String heuresHebdo = request.getParameter("heuresHebdo");
        if (heuresHebdo != null && !heuresHebdo.isEmpty()) {
            employe.setHeuresHebdo(Integer.parseInt(heuresHebdo));
        }

        // Département
        String departementId = request.getParameter("departementId");
        if (departementId != null && !departementId.isEmpty()) {
            Optional<Departement> dept = departementRepository.findByIdWithRelations(Long.parseLong(departementId));
            dept.ifPresent(employe::setDepartement);
        }

        // Poste
        String posteId = request.getParameter("posteId");
        if (posteId != null && !posteId.isEmpty()) {
            Optional<Poste> poste = posteRepository.findById(Long.parseLong(posteId));
            poste.ifPresent(employe::setPoste);
        }

        // Manager
        String managerId = request.getParameter("managerId");
        if (managerId != null && !managerId.isEmpty()) {
            Optional<Employe> manager = employeService.findByIdWithRelations(Long.parseLong(managerId));
            manager.ifPresent(employe::setManager);
        }

        // Actif
        String actif = request.getParameter("actif");
        employe.setActif(actif == null || actif.equals("true"));

        return employe;
    }

    /**
     * Charge les données nécessaires pour les formulaires
     */
    private void loadFormData(HttpServletRequest request) {
        List<Departement> departements = departementRepository.findAll();
        List<Poste> postes = posteRepository.findAll();
        List<Employe> managers = employeService.findManagers();

        request.setAttribute("departements", departements);
        request.setAttribute("postes", postes);
        request.setAttribute("managers", managers);
        request.setAttribute("roles", Role.values());
    }

    /**
     * Extrait l'action du pathInfo
     */
    private String getActionFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return "list";
        }
        return pathInfo.substring(1); // Enlever le premier '/'
    }
}

