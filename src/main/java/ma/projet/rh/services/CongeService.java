package ma.projet.rh.services;

import ma.projet.rh.entities.Conge;
import ma.projet.rh.entities.Employe;
import ma.projet.rh.entities.SoldeConge;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.StatutConge;
import ma.projet.rh.enums.TypeConge;
import ma.projet.rh.enums.TypeNotification;
import ma.projet.rh.repositories.CongeRepository;
import ma.projet.rh.repositories.SoldeCongeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des congés et du workflow de validation
 */
@Stateless
@Transactional
public class CongeService {

    private static final Logger logger = LoggerFactory.getLogger(CongeService.class);
    private static final int JOURS_CONGES_ANNUELS = 22; // Nombre de jours de congés annuels par défaut

    @Inject
    private CongeRepository congeRepository;

    @Inject
    private SoldeCongeRepository soldeCongeRepository;

    @Inject
    private NotificationService notificationService;

    /**
     * Crée une nouvelle demande de congé
     */
    public Conge creerDemande(Conge conge) {
        logger.info("Création d'une demande de congé pour l'employé: {}", conge.getEmploye().getId());

        // Valider les dates
        if (conge.getDateDebut().isAfter(conge.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        if (conge.getDateDebut().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé");
        }

        // Vérifier les chevauchements
        if (congeRepository.hasOverlap(conge.getEmploye().getId(), conge.getDateDebut(), conge.getDateFin(), null)) {
            throw new IllegalArgumentException("Ces dates chevauchent un congé existant");
        }

        // Vérifier le solde si c'est un congé annuel
        if (conge.getTypeConge() == TypeConge.ANNUEL || conge.getTypeConge().isDeduireFromSolde()) {
            if (!verifierSolde(conge.getEmploye().getId(), conge.getNombreJours())) {
                throw new IllegalArgumentException("Solde de congés insuffisant");
            }
        }

        // Définir le statut initial
        conge.setStatut(StatutConge.EN_ATTENTE);

        Conge savedConge = congeRepository.save(conge);

        // Notifier le manager
        if (conge.getEmploye().getManager() != null && 
            conge.getEmploye().getManager().getUserAccount() != null) {
            notificationService.creerNotification(
                conge.getEmploye().getManager().getUserAccount(),
                TypeNotification.CONGE_A_VALIDER,
                String.format("Nouvelle demande de congé de %s (%d jours)",
                    conge.getEmploye().getNomComplet(),
                    conge.getNombreJours()),
                "/app/conges/validation?id=" + savedConge.getId()
            );
        }

        logger.info("Demande de congé créée avec succès: {}", savedConge.getId());
        return savedConge;
    }

    /**
     * Validation par le manager
     */
    public void validerParManager(Long congeId, UserAccount manager, String commentaire) {
        logger.info("Validation manager du congé: {}", congeId);

        Conge conge = congeRepository.findByIdWithRelations(congeId)
            .orElseThrow(() -> new IllegalArgumentException("Congé non trouvé"));

        if (conge.getStatut() != StatutConge.EN_ATTENTE) {
            throw new IllegalStateException("Le congé n'est pas en attente de validation manager");
        }

        conge.validerParManager(manager, commentaire);
        congeRepository.update(conge);

        // Notifier l'employé
        if (conge.getEmploye().getUserAccount() != null) {
            notificationService.creerNotification(
                conge.getEmploye().getUserAccount(),
                TypeNotification.CONGE_APPROUVE,
                "Votre demande de congé a été validée par votre manager",
                "/app/conges/detail?id=" + conge.getId()
            );
        }

        // Notifier les RH (récupérer tous les utilisateurs avec rôle RH)
        // TODO: Implémenter la notification RH

        logger.info("Congé validé par le manager: {}", congeId);
    }

    /**
     * Validation finale par les RH
     */
    public void validerParRH(Long congeId, UserAccount rh, String commentaire) {
        logger.info("Validation RH du congé: {}", congeId);

        Conge conge = congeRepository.findByIdWithRelations(congeId)
            .orElseThrow(() -> new IllegalArgumentException("Congé non trouvé"));

        if (conge.getStatut() != StatutConge.VALIDE_MANAGER) {
            throw new IllegalStateException("Le congé doit être validé par le manager avant");
        }

        conge.validerParRH(rh, commentaire);
        congeRepository.update(conge);

        // Déduire du solde si nécessaire
        if (conge.getTypeConge().isDeduireFromSolde()) {
            deduireDuSolde(conge.getEmploye().getId(), conge.getNombreJours());
        }

        // Notifier l'employé
        if (conge.getEmploye().getUserAccount() != null) {
            notificationService.creerNotification(
                conge.getEmploye().getUserAccount(),
                TypeNotification.CONGE_APPROUVE,
                "Votre demande de congé a été approuvée",
                "/app/conges/detail?id=" + conge.getId()
            );
        }

        logger.info("Congé approuvé par les RH: {}", congeId);
    }

    /**
     * Refus d'une demande de congé
     */
    public void refuser(Long congeId, String commentaire, boolean parManager, UserAccount validateur) {
        logger.info("Refus du congé: {}", congeId);

        Conge conge = congeRepository.findByIdWithRelations(congeId)
            .orElseThrow(() -> new IllegalArgumentException("Congé non trouvé"));

        conge.refuser(commentaire, parManager);
        
        if (parManager) {
            conge.setValidateurManager(validateur);
        } else {
            conge.setValidateurRH(validateur);
        }
        
        congeRepository.update(conge);

        // Notifier l'employé
        if (conge.getEmploye().getUserAccount() != null) {
            String message = parManager 
                ? "Votre demande de congé a été refusée par votre manager" 
                : "Votre demande de congé a été refusée par les RH";
            
            notificationService.creerNotification(
                conge.getEmploye().getUserAccount(),
                TypeNotification.CONGE_REFUSE,
                message,
                "/app/conges/detail?id=" + conge.getId()
            );
        }

        logger.info("Congé refusé: {}", congeId);
    }

    /**
     * Vérifie si le solde de congés est suffisant
     */
    public boolean verifierSolde(Long employeId, int joursNecessaires) {
        int anneeActuelle = Year.now().getValue();
        Optional<SoldeConge> soldeOpt = soldeCongeRepository.findByEmployeAnnee(employeId, anneeActuelle);

        if (soldeOpt.isPresent()) {
            return soldeOpt.get().getJoursRestants() >= joursNecessaires;
        }

        return false;
    }

    /**
     * Déduit des jours du solde de congés
     */
    private void deduireDuSolde(Long employeId, int jours) {
        int anneeActuelle = Year.now().getValue();
        Optional<SoldeConge> soldeOpt = soldeCongeRepository.findByEmployeAnnee(employeId, anneeActuelle);

        if (soldeOpt.isPresent()) {
            SoldeConge solde = soldeOpt.get();
            if (solde.deduireJours(jours)) {
                soldeCongeRepository.update(solde);
                logger.info("Déduction de {} jours du solde de l'employé {}", jours, employeId);
            } else {
                logger.error("Impossible de déduire {} jours - solde insuffisant", jours);
            }
        }
    }

    /**
     * Retourne les congés d'un employé
     */
    public List<Conge> findByEmploye(Long employeId) {
        return congeRepository.findByEmploye(employeId);
    }

    /**
     * Retourne les congés en attente pour un manager
     */
    public List<Conge> findEnAttenteManager(Long managerId) {
        return congeRepository.findEnAttenteManager(managerId);
    }

    /**
     * Retourne les congés en attente de validation RH
     */
    public List<Conge> findEnAttenteRH() {
        return congeRepository.findEnAttenteRH();
    }

    /**
     * Retourne un congé par ID
     */
    public Optional<Conge> findById(Long id) {
        // Utiliser la méthode qui charge les relations
        return congeRepository.findByIdWithRelations(id);
    }

    /**
     * Retourne les congés pour une période
     */
    public List<Conge> findByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return congeRepository.findByPeriode(dateDebut, dateFin);
    }

    /**
     * Compte les congés en attente pour un manager
     */
    public long countEnAttenteManager(Long managerId) {
        return congeRepository.countEnAttenteManager(managerId);
    }

    /**
     * Compte les congés en attente RH
     */
    public long countEnAttenteRH() {
        return congeRepository.countEnAttenteRH();
    }

    /**
     * Initialise le solde de congés pour un employé
     */
    public void initialiserSoldeConge(Employe employe, int annee) {
        Optional<SoldeConge> existant = soldeCongeRepository.findByEmployeAnnee(employe.getId(), annee);
        
        if (!existant.isPresent()) {
            SoldeConge solde = new SoldeConge(employe, annee, JOURS_CONGES_ANNUELS);
            soldeCongeRepository.save(solde);
            logger.info("Solde de congés initialisé pour l'employé {} année {}", employe.getId(), annee);
        }
    }

    /**
     * Retourne le solde de congés d'un employé pour l'année en cours
     */
    public Optional<SoldeConge> getSoldeActuel(Long employeId) {
        return soldeCongeRepository.findByEmployeAnnee(employeId, Year.now().getValue());
    }
}

