package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.Poste;
import ma.projet.rh.repositories.PosteRepository;
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
import java.util.List;
import java.util.Optional;

/**
 * Servlet pour la gestion des postes (CRUD)
 */
public class PosteServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PosteServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private PosteRepository posteRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());

        try {
            switch (action) {
                case "list":
                    listPostes(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    listPostes(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans PosteServlet (GET)", e);
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
                    createPoste(request, response);
                    break;
                case "edit":
                    updatePoste(request, response);
                    break;
                case "delete":
                    deletePoste(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue");
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans PosteServlet (POST)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void listPostes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Poste> postes = posteRepository.findAll();
        request.setAttribute("postes", postes);
        request.getRequestDispatcher("/WEB-INF/views/postes/list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/postes/create.jsp").forward(request, response);
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
            Optional<Poste> posteOpt = posteRepository.findById(id);

        if (posteOpt.isPresent()) {
            request.setAttribute("poste", posteOpt.get());
            request.getRequestDispatcher("/WEB-INF/views/postes/edit.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Poste non trouvé");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void createPoste(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            Poste poste = new Poste();
            poste.setTitre(request.getParameter("titre"));
            poste.setDescription(request.getParameter("description"));

            String salaireMin = request.getParameter("salaireMin");
            if (salaireMin != null && !salaireMin.isEmpty()) {
                poste.setSalaireMin(new BigDecimal(salaireMin));
            }

            String salaireMax = request.getParameter("salaireMax");
            if (salaireMax != null && !salaireMax.isEmpty()) {
                poste.setSalaireMax(new BigDecimal(salaireMax));
            }

            posteRepository.save(poste);
            logger.info("Poste créé avec succès: {}", poste.getId());

            response.sendRedirect(request.getContextPath() + "/app/postes/list?success=created");

        } catch (Exception e) {
            logger.error("Erreur lors de la création du poste", e);
            request.setAttribute("error", e.getMessage());
            showCreateForm(request, response);
        }
    }

    private void updatePoste(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
                return;
            }
            Long id = Long.parseLong(idParam);
            Optional<Poste> posteOpt = posteRepository.findById(id);

            if (!posteOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Poste non trouvé");
                return;
            }

            Poste poste = posteOpt.get();
            poste.setTitre(request.getParameter("titre"));
            poste.setDescription(request.getParameter("description"));

            String salaireMin = request.getParameter("salaireMin");
            if (salaireMin != null && !salaireMin.isEmpty()) {
                poste.setSalaireMin(new BigDecimal(salaireMin));
            } else {
                poste.setSalaireMin(null);
            }

            String salaireMax = request.getParameter("salaireMax");
            if (salaireMax != null && !salaireMax.isEmpty()) {
                poste.setSalaireMax(new BigDecimal(salaireMax));
            } else {
                poste.setSalaireMax(null);
            }

            posteRepository.update(poste);
            logger.info("Poste mis à jour avec succès: {}", id);

            response.sendRedirect(request.getContextPath() + "/app/postes/list?success=updated");

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du poste", e);
            request.setAttribute("error", e.getMessage());
            showEditForm(request, response);
        }
    }

    private void deletePoste(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            
            // Vérifier s'il y a des employés avec ce poste
        long count = posteRepository.countEmployes(id);
        if (count > 0) {
            logger.warn("Impossible de supprimer le poste {} : {} employés associés", id, count);
            response.sendRedirect(request.getContextPath() + "/app/postes/list?error=hasEmployees");
            return;
        }

            Optional<Poste> posteOpt = posteRepository.findById(id);
            if (posteOpt.isPresent()) {
                posteRepository.delete(posteOpt.get());
                logger.info("Poste supprimé: {}", id);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Poste non trouvé");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/app/postes/list?success=deleted");
    }

    private String getActionFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return "list";
        }
        return pathInfo.substring(1);
    }
}

