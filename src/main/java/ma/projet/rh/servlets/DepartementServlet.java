package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.Departement;
import ma.projet.rh.entities.Employe;
import ma.projet.rh.repositories.DepartementRepository;
import ma.projet.rh.repositories.EmployeRepository;
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
import java.util.Optional;

/**
 * Servlet pour la gestion des départements (CRUD)
 */
public class DepartementServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DepartementServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private DepartementRepository departementRepository;

    @EJB
    private EmployeRepository employeRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());

        try {
            switch (action) {
                case "list":
                    listDepartements(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    listDepartements(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans DepartementServlet (GET)", e);
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
                    createDepartement(request, response);
                    break;
                case "edit":
                    updateDepartement(request, response);
                    break;
                case "delete":
                    deleteDepartement(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue");
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans DepartementServlet (POST)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void listDepartements(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Departement> departements = departementRepository.findAll();
        request.setAttribute("departements", departements);
        request.getRequestDispatcher("/WEB-INF/views/departements/list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Employe> managers = employeRepository.findManagers();
        request.setAttribute("managers", managers);
        request.getRequestDispatcher("/WEB-INF/views/departements/create.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Optional<Departement> deptOpt = departementRepository.findByIdWithRelations(id);

        if (deptOpt.isPresent()) {
            request.setAttribute("departement", deptOpt.get());
            List<Employe> managers = employeRepository.findManagers();
            request.setAttribute("managers", managers);
            request.getRequestDispatcher("/WEB-INF/views/departements/edit.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Département non trouvé");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void createDepartement(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            Departement dept = new Departement();
            dept.setNom(request.getParameter("nom"));
            dept.setDescription(request.getParameter("description"));

            String responsableId = request.getParameter("responsableId");
            if (responsableId != null && !responsableId.isEmpty()) {
                Optional<Employe> responsable = employeRepository.findByIdWithRelations(Long.parseLong(responsableId));
                responsable.ifPresent(dept::setResponsable);
            }

            departementRepository.save(dept);
            logger.info("Département créé avec succès: {}", dept.getId());

            response.sendRedirect(request.getContextPath() + "/app/departements/list?success=created");

        } catch (Exception e) {
            logger.error("Erreur lors de la création du département", e);
            request.setAttribute("error", e.getMessage());
            showCreateForm(request, response);
        }
    }

    private void updateDepartement(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
                return;
            }
            Long id = Long.parseLong(idParam);
            Optional<Departement> deptOpt = departementRepository.findByIdWithRelations(id);

            if (deptOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Département non trouvé");
                return;
            }

            Departement dept = deptOpt.get();
            dept.setNom(request.getParameter("nom"));
            dept.setDescription(request.getParameter("description"));

            String responsableId = request.getParameter("responsableId");
            if (responsableId != null && !responsableId.isEmpty()) {
                Optional<Employe> responsable = employeRepository.findByIdWithRelations(Long.parseLong(responsableId));
                responsable.ifPresent(dept::setResponsable);
            } else {
                dept.setResponsable(null);
            }

            departementRepository.update(dept);
            logger.info("Département mis à jour avec succès: {}", id);

            response.sendRedirect(request.getContextPath() + "/app/departements/list?success=updated");

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du département", e);
            request.setAttribute("error", e.getMessage());
            showEditForm(request, response);
        }
    }

    private void deleteDepartement(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            
            // Vérifier s'il y a des employés dans ce département
        long count = departementRepository.countEmployes(id);
        if (count > 0) {
            logger.warn("Impossible de supprimer le département {} : {} employés associés", id, count);
            response.sendRedirect(request.getContextPath() + "/app/departements/list?error=hasEmployees");
            return;
        }

            Optional<Departement> deptOpt = departementRepository.findByIdWithRelations(id);
            if (deptOpt.isPresent()) {
                departementRepository.delete(deptOpt.get());
                logger.info("Département supprimé: {}", id);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Département non trouvé");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/app/departements/list?success=deleted");
    }

    private String getActionFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return "list";
        }
        return pathInfo.substring(1);
    }
}

