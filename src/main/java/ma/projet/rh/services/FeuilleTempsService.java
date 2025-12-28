package ma.projet.rh.services;

import ma.projet.rh.entities.FeuilleTemps;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.StatutFeuilleTemps;
import ma.projet.rh.enums.TypeNotification;
import ma.projet.rh.repositories.FeuilleTempsRepository;
import ma.projet.rh.repositories.NotificationRepository;
import ma.projet.rh.repositories.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service métier pour la gestion des feuilles de temps
 */
@Stateless
@Transactional
public class FeuilleTempsService {

    private static final Logger logger = LoggerFactory.getLogger(FeuilleTempsService.class);
    private static final int HEURES_NORMALES_SEMAINE = 35;
    private static final BigDecimal TAUX_HEURES_SUP = new BigDecimal("1.5"); // 50% de majoration

    @Inject
    private FeuilleTempsRepository feuilleTempsRepository;

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserAccountRepository userAccountRepository;

    /**
     * Crée une nouvelle feuille de temps
     */
    public FeuilleTemps creer(FeuilleTemps feuilleTemps) {
        logger.info("Création d'une feuille de temps pour l'employé: {}", 
            feuilleTemps.getEmploye().getId());

        // Validation
        if (feuilleTemps.getDateSemaine() == null) {
            throw new IllegalArgumentException("La date de semaine est obligatoire");
        }

        if (feuilleTemps.getHeuresNormales() == null || feuilleTemps.getHeuresNormales().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Les heures normales doivent être positives");
        }

        // Vérifier qu'il n'existe pas déjà une feuille pour cette semaine
        LocalDate dateSemaine = feuilleTemps.getDateSemaine();
        Long employeId = feuilleTemps.getEmploye().getId();
        
        // Vérifier l'existence en parcourant les feuilles existantes
        List<FeuilleTemps> existing = feuilleTempsRepository.findByEmploye(employeId);
        boolean exists = existing.stream()
            .anyMatch(f -> f.getDateSemaine().equals(dateSemaine));
        
        if (exists) {
            throw new IllegalArgumentException(
                "Une feuille de temps existe déjà pour cette semaine"
            );
        }

        // Calculer les heures supplémentaires
        calculerHeuresSupplementaires(feuilleTemps);

        // Statut initial : BROUILLON
        feuilleTemps.setStatut(StatutFeuilleTemps.BROUILLON);
        feuilleTemps.setDateSaisie(java.time.LocalDateTime.now());

        FeuilleTemps saved = feuilleTempsRepository.save(feuilleTemps);
        logger.info("Feuille de temps créée avec succès: {}", saved.getId());

        return saved;
    }

    /**
     * Soumet une feuille de temps pour validation
     */
    public void soumettre(Long feuilleTempsId) {
        logger.info("Soumission de la feuille de temps: {}", feuilleTempsId);

        Optional<FeuilleTemps> feuilleOpt = feuilleTempsRepository.findById(feuilleTempsId);
        if (feuilleOpt.isEmpty()) {
            throw new IllegalArgumentException("Feuille de temps non trouvée");
        }

        FeuilleTemps feuille = feuilleOpt.get();

        if (feuille.getStatut() != StatutFeuilleTemps.BROUILLON) {
            throw new IllegalStateException(
                "Seules les feuilles en brouillon peuvent être soumises"
            );
        }

        feuille.soumettre();
        feuilleTempsRepository.update(feuille);

        // Notifier le manager
        if (feuille.getEmploye().getManager() != null && 
            feuille.getEmploye().getManager().getUserAccount() != null) {
            notificationService.creerNotification(
                feuille.getEmploye().getManager().getUserAccount(),
                TypeNotification.TEMPS_A_VALIDER,
                String.format("Nouvelle feuille de temps à valider de %s (Semaine du %s)",
                    feuille.getEmploye().getNomComplet(),
                    feuille.getDateSemaine()),
                "/app/temps/validation?id=" + feuille.getId()
            );
        }

        logger.info("Feuille de temps soumise avec succès: {}", feuilleTempsId);
    }

    /**
     * Valide une feuille de temps
     */
    public void valider(Long feuilleTempsId, UserAccount validateur, String commentaire) {
        logger.info("Validation de la feuille de temps: {} par {}", feuilleTempsId, validateur.getUsername());

        Optional<FeuilleTemps> feuilleOpt = feuilleTempsRepository.findByIdWithRelations(feuilleTempsId);
        if (feuilleOpt.isEmpty()) {
            throw new IllegalArgumentException("Feuille de temps non trouvée");
        }

        FeuilleTemps feuille = feuilleOpt.get();

        if (feuille.getStatut() != StatutFeuilleTemps.SOUMIS) {
            throw new IllegalStateException(
                "Seules les feuilles soumises peuvent être validées"
            );
        }

        feuille.valider(validateur, commentaire != null ? commentaire : "");
        feuilleTempsRepository.update(feuille);

        // Notifier l'employé
        if (feuille.getEmploye().getUserAccount() != null) {
            notificationService.creerNotification(
                feuille.getEmploye().getUserAccount(),
                TypeNotification.TEMPS_VALIDEE,
                String.format("Votre feuille de temps (Semaine du %s) a été validée",
                    feuille.getDateSemaine()),
                "/app/temps/view?id=" + feuille.getId()
            );
        }

        logger.info("Feuille de temps validée avec succès: {}", feuilleTempsId);
    }

    /**
     * Rejette une feuille de temps
     */
    public void rejeter(Long feuilleTempsId, String commentaire, UserAccount validateur) {
        logger.info("Rejet de la feuille de temps: {} par {}", feuilleTempsId, validateur.getUsername());

        Optional<FeuilleTemps> feuilleOpt = feuilleTempsRepository.findByIdWithRelations(feuilleTempsId);
        if (feuilleOpt.isEmpty()) {
            throw new IllegalArgumentException("Feuille de temps non trouvée");
        }

        FeuilleTemps feuille = feuilleOpt.get();

        if (feuille.getStatut() != StatutFeuilleTemps.SOUMIS) {
            throw new IllegalStateException(
                "Seules les feuilles soumises peuvent être rejetées"
            );
        }

        feuille.rejeter(commentaire);
        feuilleTempsRepository.update(feuille);

        // Notifier l'employé
        if (feuille.getEmploye().getUserAccount() != null) {
            notificationService.creerNotification(
                feuille.getEmploye().getUserAccount(),
                TypeNotification.TEMPS_VALIDEE, // On pourrait créer un type TEMPS_REJETEE
                String.format("Votre feuille de temps (Semaine du %s) a été rejetée. Commentaire: %s",
                    feuille.getDateSemaine(), commentaire),
                "/app/temps/view?id=" + feuille.getId()
            );
        }

        logger.info("Feuille de temps rejetée: {}", feuilleTempsId);
    }

    /**
     * Calcule les heures supplémentaires d'une feuille de temps
     */
    public void calculerHeuresSupplementaires(FeuilleTemps feuilleTemps) {
        if (feuilleTemps.getHeuresNormales() == null) {
            feuilleTemps.setHeuresSupplementaires(BigDecimal.ZERO);
            return;
        }

        BigDecimal heuresNormales = feuilleTemps.getHeuresNormales();
        BigDecimal heuresMax = BigDecimal.valueOf(HEURES_NORMALES_SEMAINE);

        if (heuresNormales.compareTo(heuresMax) > 0) {
            BigDecimal heuresSup = heuresNormales.subtract(heuresMax);
            feuilleTemps.setHeuresSupplementaires(heuresSup.setScale(2, RoundingMode.HALF_UP));
        } else {
            feuilleTemps.setHeuresSupplementaires(BigDecimal.ZERO);
        }
    }

    /**
     * Récupère le total des heures d'un employé pour un mois donné
     */
    public BigDecimal getHeuresMois(Long employeId, int mois, int annee) {
        List<FeuilleTemps> feuilles = feuilleTempsRepository.findByEmployeAndPeriode(employeId, mois, annee);
        
        BigDecimal total = BigDecimal.ZERO;
        for (FeuilleTemps feuille : feuilles) {
            if (feuille.getStatut() == StatutFeuilleTemps.VALIDE) {
                if (feuille.getHeuresNormales() != null) {
                    total = total.add(feuille.getHeuresNormales());
                }
                if (feuille.getHeuresSupplementaires() != null) {
                    total = total.add(feuille.getHeuresSupplementaires());
                }
            }
        }
        
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Récupère les heures supplémentaires d'un employé pour un mois donné
     */
    public BigDecimal getHeuresSupplementairesMois(Long employeId, int mois, int annee) {
        List<FeuilleTemps> feuilles = feuilleTempsRepository.findByEmployeAndPeriode(employeId, mois, annee);
        
        BigDecimal total = BigDecimal.ZERO;
        for (FeuilleTemps feuille : feuilles) {
            if (feuille.getStatut() == StatutFeuilleTemps.VALIDE && 
                feuille.getHeuresSupplementaires() != null) {
                total = total.add(feuille.getHeuresSupplementaires());
            }
        }
        
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Récupère toutes les feuilles de temps d'un employé
     */
    public List<FeuilleTemps> findByEmploye(Long employeId) {
        return feuilleTempsRepository.findByEmploye(employeId);
    }

    /**
     * Récupère une feuille de temps par son ID avec toutes les relations
     */
    public Optional<FeuilleTemps> findByIdWithRelations(Long id) {
        return feuilleTempsRepository.findByIdWithRelations(id);
    }

    /**
     * Récupère les feuilles de temps en attente de validation pour un manager
     */
    public List<FeuilleTemps> findEnAttenteValidation(Long managerId) {
        return feuilleTempsRepository.findEnAttenteValidation(managerId);
    }

    /**
     * Met à jour une feuille de temps
     */
    public FeuilleTemps update(FeuilleTemps feuilleTemps) {
        if (feuilleTemps.getStatut() != StatutFeuilleTemps.BROUILLON) {
            throw new IllegalStateException(
                "Seules les feuilles en brouillon peuvent être modifiées"
            );
        }

        // Recalculer les heures sup
        calculerHeuresSupplementaires(feuilleTemps);

        return feuilleTempsRepository.update(feuilleTemps);
    }

    /**
     * Supprime une feuille de temps (seulement si en brouillon)
     */
    public void delete(Long id) {
        Optional<FeuilleTemps> feuilleOpt = feuilleTempsRepository.findById(id);
        if (feuilleOpt.isEmpty()) {
            throw new IllegalArgumentException("Feuille de temps non trouvée");
        }

        FeuilleTemps feuille = feuilleOpt.get();
        if (feuille.getStatut() != StatutFeuilleTemps.BROUILLON) {
            throw new IllegalStateException(
                "Seules les feuilles en brouillon peuvent être supprimées"
            );
        }

        feuilleTempsRepository.delete(feuille);
    }
}

