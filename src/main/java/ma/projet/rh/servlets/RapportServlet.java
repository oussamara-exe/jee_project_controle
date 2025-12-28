package ma.projet.rh.servlets;

import ma.projet.rh.dto.UserSessionDTO;
import ma.projet.rh.entities.Conge;
import ma.projet.rh.entities.Employe;
import ma.projet.rh.entities.FeuilleTemps;
import ma.projet.rh.entities.FichePaie;
import ma.projet.rh.enums.Role;
import ma.projet.rh.enums.StatutConge;
import ma.projet.rh.enums.StatutFeuilleTemps;
import ma.projet.rh.enums.StatutFichePaie;
import ma.projet.rh.repositories.CongeRepository;
import ma.projet.rh.repositories.EmployeRepository;
import ma.projet.rh.repositories.FeuilleTempsRepository;
import ma.projet.rh.repositories.FichePaieRepository;
import ma.projet.rh.services.EmployeService;
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
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet pour les rapports et statistiques
 */
public class RapportServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RapportServlet.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @EJB
    private EmployeService employeService;

    @EJB
    private EmployeRepository employeRepository;

    @EJB
    private CongeRepository congeRepository;

    @EJB
    private FeuilleTempsRepository feuilleTempsRepository;

    @EJB
    private FichePaieRepository fichePaieRepository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromPath(request.getPathInfo());
        UserSessionDTO currentUser = getCurrentUser(request);

        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        try {
            switch (action) {
                case "employes":
                    showRapportEmployes(request, response);
                    break;
                case "conges":
                    showRapportConges(request, response);
                    break;
                case "paie":
                    showRapportPaie(request, response);
                    break;
                case "":
                case "index":
                default:
                    showIndex(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Erreur dans RapportServlet (GET)", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Affiche la page d'index des rapports
     */
    private void showIndex(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/rapports/index.jsp").forward(request, response);
    }

    /**
     * Rapport sur les employés
     */
    private void showRapportEmployes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Statistiques générales
        long totalEmployes = employeService.countActifs();
        long totalDepartements = employeRepository.countDepartements();
        long totalPostes = employeRepository.countPostes();
        
        // Répartition par département
        List<Object[]> repartitionDept = employeRepository.countByDepartement();
        
        // Répartition par poste
        List<Object[]> repartitionPoste = employeRepository.countByPoste();
        
        // Taux d'activité
        long totalInactifs = employeRepository.countInactifs();
        long total = totalEmployes + totalInactifs;
        double tauxActif = total > 0 ? (totalEmployes * 100.0 / total) : 0;

        // Liste de tous les employés pour le détail
        List<ma.projet.rh.entities.Employe> tousEmployes = employeRepository.findAll();

        request.setAttribute("totalEmployes", totalEmployes);
        request.setAttribute("totalDepartements", totalDepartements);
        request.setAttribute("totalPostes", totalPostes);
        request.setAttribute("totalInactifs", totalInactifs);
        request.setAttribute("tauxActif", tauxActif);
        request.setAttribute("repartitionDept", repartitionDept);
        request.setAttribute("repartitionPoste", repartitionPoste);
        request.setAttribute("tousEmployes", tousEmployes);

        request.getRequestDispatcher("/WEB-INF/views/rapports/employes.jsp").forward(request, response);
    }

    /**
     * Rapport sur les congés
     */
    private void showRapportConges(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int annee = request.getParameter("annee") != null ? 
            Integer.parseInt(request.getParameter("annee")) : LocalDate.now().getYear();

        // Statistiques générales
        long totalConges = congeRepository.countAll();
        long congesEnAttente = congeRepository.countByStatut(StatutConge.EN_ATTENTE);
        long congesValides = congeRepository.countByStatut(StatutConge.VALIDE_MANAGER);
        long congesApprouves = congeRepository.countByStatut(StatutConge.APPROUVE);
        long congesRefuses = congeRepository.countByStatut(StatutConge.REFUSE);

        // Congés par type
        List<Object[]> congesParType = congeRepository.countByType();
        
        // Congés par mois de l'année
        List<Object[]> congesParMois = congeRepository.countByMois(annee);

        // Top employés avec le plus de congés
        List<Object[]> topConges = congeRepository.findTopEmployesWithConges(5);

        // Liste de tous les congés (avec relations chargées)
        List<Conge> tousConges = congeRepository.findAllWithRelations();

        request.setAttribute("annee", annee);
        request.setAttribute("totalConges", totalConges);
        request.setAttribute("congesEnAttente", congesEnAttente);
        request.setAttribute("congesValides", congesValides);
        request.setAttribute("congesApprouves", congesApprouves);
        request.setAttribute("congesRefuses", congesRefuses);
        request.setAttribute("congesParType", congesParType);
        request.setAttribute("congesParMois", congesParMois);
        request.setAttribute("topConges", topConges);
        request.setAttribute("tousConges", tousConges);

        request.getRequestDispatcher("/WEB-INF/views/rapports/conges.jsp").forward(request, response);
    }

    /**
     * Rapport sur la paie
     */
    private void showRapportPaie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int annee = request.getParameter("annee") != null ? 
            Integer.parseInt(request.getParameter("annee")) : LocalDate.now().getYear();
        int mois = request.getParameter("mois") != null ? 
            Integer.parseInt(request.getParameter("mois")) : LocalDate.now().getMonthValue();

        // Statistiques générales
        long totalFiches = fichePaieRepository.countAll();
        long fichesCalculees = fichePaieRepository.countByStatut(StatutFichePaie.CALCULE);
        long fichesValidees = fichePaieRepository.countByStatut(StatutFichePaie.VALIDE);
        long fichesPayees = fichePaieRepository.countByStatut(StatutFichePaie.PAYE);

        // Fiches de paie pour le mois sélectionné
        List<FichePaie> fichesMois = fichePaieRepository.findByPeriode(mois, annee);
        
        // Statistiques financières
        double totalBrut = 0;
        double totalDeductions = 0;
        double totalNet = 0;
        
        for (FichePaie fiche : fichesMois) {
            if (fiche.getTotalBrut() != null) {
                totalBrut += fiche.getTotalBrut().doubleValue();
            }
            if (fiche.getTotalDeductions() != null) {
                totalDeductions += fiche.getTotalDeductions().doubleValue();
            }
            if (fiche.getNetAPayer() != null) {
                totalNet += fiche.getNetAPayer().doubleValue();
            }
        }

        // Répartition par département
        List<Object[]> repartitionDept = fichePaieRepository.sumByDepartement(mois, annee);

        request.setAttribute("annee", annee);
        request.setAttribute("mois", mois);
        request.setAttribute("totalFiches", totalFiches);
        request.setAttribute("fichesCalculees", fichesCalculees);
        request.setAttribute("fichesValidees", fichesValidees);
        request.setAttribute("fichesPayees", fichesPayees);
        request.setAttribute("fichesMois", fichesMois);
        request.setAttribute("totalBrut", totalBrut);
        request.setAttribute("totalDeductions", totalDeductions);
        request.setAttribute("totalNet", totalNet);
        request.setAttribute("repartitionDept", repartitionDept);

        request.getRequestDispatcher("/WEB-INF/views/rapports/paie.jsp").forward(request, response);
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
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            return "index";
        }
        String path = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        if (path.contains("/")) {
            return path.substring(0, path.indexOf("/"));
        }
        return path.isEmpty() ? "index" : path;
    }
}
