package ma.projet.rh.services;

import ma.projet.rh.entities.Employe;
import ma.projet.rh.entities.FeuilleTemps;
import ma.projet.rh.entities.FichePaie;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.StatutFichePaie;
import ma.projet.rh.enums.TypeNotification;
import ma.projet.rh.repositories.EmployeRepository;
import ma.projet.rh.repositories.FeuilleTempsRepository;
import ma.projet.rh.repositories.FichePaieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des fiches de paie
 */
@Stateless
@Transactional
public class FichePaieService {

    private static final Logger logger = LoggerFactory.getLogger(FichePaieService.class);
    
    // Taux de cotisations sociales (21%)
    private static final BigDecimal TAUX_COTISATIONS = new BigDecimal("0.21");
    
    // Taux de majoration des heures supplémentaires (50%)
    private static final BigDecimal TAUX_HEURES_SUP = new BigDecimal("1.5");
    
    // Taux de prime d'ancienneté (2% par année après 5 ans, max 20%)
    private static final BigDecimal TAUX_PRIME_ANCIENNETE = new BigDecimal("0.02");
    private static final int ANNEES_MIN_ANCIENNETE = 5;
    private static final BigDecimal TAUX_MAX_ANCIENNETE = new BigDecimal("0.20");

    @Inject
    private FichePaieRepository fichePaieRepository;

    @Inject
    private EmployeRepository employeRepository;

    @Inject
    private FeuilleTempsRepository feuilleTempsRepository;

    @Inject
    private NotificationService notificationService;

    /**
     * Génère une fiche de paie pour un employé pour un mois donné
     */
    public FichePaie genererFichePaie(Long employeId, int mois, int annee) {
        logger.info("Génération de la fiche de paie pour l'employé: {}, mois: {}/{}", 
            employeId, mois, annee);

        // Vérifier si une fiche existe déjà
        Optional<FichePaie> existingOpt = fichePaieRepository.findByEmployeAndPeriode(employeId, mois, annee);
        if (existingOpt.isPresent()) {
            throw new IllegalArgumentException(
                "Une fiche de paie existe déjà pour cet employé pour cette période"
            );
        }

        // Récupérer l'employé
        Optional<Employe> employeOpt = employeRepository.findByIdWithRelations(employeId);
        if (!employeOpt.isPresent()) {
            throw new IllegalArgumentException("Employé non trouvé");
        }

        Employe employe = employeOpt.get();

        // Créer la fiche de paie
        FichePaie fiche = new FichePaie();
        fiche.setEmploye(employe);
        fiche.setMois(mois);
        fiche.setAnnee(annee);
        fiche.setSalaireBase(employe.getSalaireBase());
        fiche.setDateGeneration(LocalDateTime.now());
        fiche.setStatut(StatutFichePaie.CALCULE);

        // Calculer les heures supplémentaires du mois
        BigDecimal heuresSup = calculerHeuresSup(employeId, mois, annee);
        fiche.setHeuresSupplementaires(heuresSup);

        // Calculer le montant des heures sup
        if (heuresSup.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tauxHoraire = employe.getSalaireBase()
                .divide(BigDecimal.valueOf(employe.getHeuresHebdo() * 4.33), 2, RoundingMode.HALF_UP);
            BigDecimal montantHeuresSup = heuresSup
                .multiply(tauxHoraire)
                .multiply(TAUX_HEURES_SUP);
            fiche.setMontantHeuresSup(montantHeuresSup.setScale(2, RoundingMode.HALF_UP));
        } else {
            fiche.setMontantHeuresSup(BigDecimal.ZERO);
        }

        // Calculer la prime d'ancienneté
        BigDecimal primeAnciennete = calculerPrimeAnciennete(employe);
        fiche.setPrimeAnciennete(primeAnciennete);

        // Calculer tous les montants
        calculerMontants(fiche);

        // Sauvegarder
        FichePaie saved = fichePaieRepository.save(fiche);
        
        logger.info("Fiche de paie générée avec succès: {}", saved.getId());
        
        return saved;
    }

    /**
     * Génère les fiches de paie pour tous les employés actifs pour un mois donné
     */
    public int genererFichesPourMois(int mois, int annee) {
        logger.info("Génération des fiches de paie pour le mois: {}/{}", mois, annee);

        List<Employe> employes = employeRepository.findActifs();
        int count = 0;

        for (Employe employe : employes) {
            try {
                genererFichePaie(employe.getId(), mois, annee);
                count++;
            } catch (IllegalArgumentException e) {
                // Fiche déjà existante, on continue
                logger.warn("Fiche déjà existante pour l'employé {}: {}", 
                    employe.getId(), e.getMessage());
            } catch (Exception e) {
                logger.error("Erreur lors de la génération de la fiche pour l'employé {}", 
                    employe.getId(), e);
            }
        }

        logger.info("{} fiches de paie générées pour {}/{}", count, mois, annee);
        return count;
    }

    /**
     * Calcule le salaire brut d'un employé pour un mois donné
     */
    public BigDecimal calculerSalaireBrut(Employe employe, int mois, int annee) {
        BigDecimal brut = employe.getSalaireBase();

        // Ajouter les heures sup
        BigDecimal heuresSup = calculerHeuresSup(employe.getId(), mois, annee);
        if (heuresSup.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tauxHoraire = employe.getSalaireBase()
                .divide(BigDecimal.valueOf(employe.getHeuresHebdo() * 4.33), 2, RoundingMode.HALF_UP);
            BigDecimal montantHeuresSup = heuresSup
                .multiply(tauxHoraire)
                .multiply(TAUX_HEURES_SUP);
            brut = brut.add(montantHeuresSup);
        }

        // Ajouter la prime d'ancienneté
        brut = brut.add(calculerPrimeAnciennete(employe));

        return brut.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule les heures supplémentaires d'un employé pour un mois donné
     */
    public BigDecimal calculerHeuresSup(Long employeId, int mois, int annee) {
        List<FeuilleTemps> feuilles = feuilleTempsRepository.findByEmployeAndPeriode(employeId, mois, annee);
        
        BigDecimal total = BigDecimal.ZERO;
        for (FeuilleTemps feuille : feuilles) {
            if (feuille.getStatut() == ma.projet.rh.enums.StatutFeuilleTemps.VALIDE && 
                feuille.getHeuresSupplementaires() != null) {
                total = total.add(feuille.getHeuresSupplementaires());
            }
        }
        
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule les cotisations sociales (21% du brut)
     */
    public BigDecimal calculerCotisations(BigDecimal brut) {
        if (brut == null || brut.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return brut.multiply(TAUX_COTISATIONS).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule l'impôt sur le revenu selon un barème progressif
     */
    public BigDecimal calculerIR(BigDecimal brut) {
        if (brut == null || brut.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Barème progressif simplifié
        if (brut.compareTo(new BigDecimal("5000")) < 0) {
            return BigDecimal.ZERO;
        } else if (brut.compareTo(new BigDecimal("10000")) < 0) {
            // Tranche 5000-10000 : 10%
            return brut.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        } else if (brut.compareTo(new BigDecimal("20000")) < 0) {
            // Tranche 10000-20000 : 20%
            return brut.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        } else {
            // Au-dessus de 20000 : 30%
            return brut.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
        }
    }

    /**
     * Calcule la prime d'ancienneté d'un employé
     */
    public BigDecimal calculerPrimeAnciennete(Employe employe) {
        if (employe.getDateEmbauche() == null) {
            return BigDecimal.ZERO;
        }

        int annees = java.time.Period.between(employe.getDateEmbauche(), LocalDate.now()).getYears();
        
        if (annees < ANNEES_MIN_ANCIENNETE) {
            return BigDecimal.ZERO;
        }

        // 2% par année après 5 ans, maximum 20%
        BigDecimal taux = TAUX_PRIME_ANCIENNETE.multiply(BigDecimal.valueOf(annees - ANNEES_MIN_ANCIENNETE + 1));
        if (taux.compareTo(TAUX_MAX_ANCIENNETE) > 0) {
            taux = TAUX_MAX_ANCIENNETE;
        }

        return employe.getSalaireBase()
            .multiply(taux)
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule tous les montants d'une fiche de paie
     */
    public void calculerMontants(FichePaie fiche) {
        // Calcul du brut
        BigDecimal totalBrut = fiche.getSalaireBase()
            .add(fiche.getMontantHeuresSup() != null ? fiche.getMontantHeuresSup() : BigDecimal.ZERO)
            .add(fiche.getPrimes() != null ? fiche.getPrimes() : BigDecimal.ZERO)
            .add(fiche.getIndemnites() != null ? fiche.getIndemnites() : BigDecimal.ZERO)
            .add(fiche.getPrimeAnciennete() != null ? fiche.getPrimeAnciennete() : BigDecimal.ZERO);

        fiche.setTotalBrut(totalBrut.setScale(2, RoundingMode.HALF_UP));

        // Cotisations sociales (21%)
        BigDecimal cotisations = calculerCotisations(totalBrut);
        fiche.setCotisationsSociales(cotisations);

        // Impôt sur le revenu
        BigDecimal ir = calculerIR(totalBrut);
        fiche.setRetenueIR(ir);

        // Total déductions
        BigDecimal totalDeductions = cotisations
            .add(ir)
            .add(fiche.getAutresDeductions() != null ? fiche.getAutresDeductions() : BigDecimal.ZERO);

        fiche.setTotalDeductions(totalDeductions.setScale(2, RoundingMode.HALF_UP));

        // Net à payer
        BigDecimal netAPayer = totalBrut.subtract(totalDeductions);
        fiche.setNetAPayer(netAPayer.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Valide une fiche de paie
     */
    public void valider(Long ficheId, UserAccount validateur) {
        logger.info("Validation de la fiche de paie: {} par {}", ficheId, validateur.getUsername());

        Optional<FichePaie> ficheOpt = fichePaieRepository.findByIdWithRelations(ficheId);
        if (!ficheOpt.isPresent()) {
            throw new IllegalArgumentException("Fiche de paie non trouvée");
        }

        FichePaie fiche = ficheOpt.get();

        if (fiche.getStatut() != StatutFichePaie.CALCULE) {
            throw new IllegalStateException(
                "Seules les fiches calculées peuvent être validées"
            );
        }

        fiche.valider(validateur);
        fichePaieRepository.update(fiche);

        // Notifier l'employé
        if (fiche.getEmploye().getUserAccount() != null) {
            notificationService.creerNotification(
                fiche.getEmploye().getUserAccount(),
                TypeNotification.PAIE_VALIDEE,
                String.format("Votre fiche de paie de %d/%d a été validée",
                    fiche.getMois(), fiche.getAnnee()),
                "/app/paie/view?id=" + fiche.getId()
            );
        }

        logger.info("Fiche de paie validée avec succès: {}", ficheId);
    }

    /**
     * Marque une fiche de paie comme payée
     */
    public void marquerCommePaye(Long ficheId) {
        logger.info("Marquage de la fiche de paie comme payée: {}", ficheId);

        Optional<FichePaie> ficheOpt = fichePaieRepository.findByIdWithRelations(ficheId);
        if (!ficheOpt.isPresent()) {
            throw new IllegalArgumentException("Fiche de paie non trouvée");
        }

        FichePaie fiche = ficheOpt.get();

        if (fiche.getStatut() != StatutFichePaie.VALIDE) {
            throw new IllegalStateException(
                "Seules les fiches validées peuvent être marquées comme payées"
            );
        }

        fiche.marquerCommePaye(LocalDate.now());
        fichePaieRepository.update(fiche);

        logger.info("Fiche de paie marquée comme payée: {}", ficheId);
    }

    /**
     * Récupère une fiche de paie par son ID avec toutes les relations
     */
    public Optional<FichePaie> findByIdWithRelations(Long id) {
        return fichePaieRepository.findByIdWithRelations(id);
    }

    /**
     * Récupère toutes les fiches de paie d'un employé
     */
    public List<FichePaie> findByEmploye(Long employeId) {
        return fichePaieRepository.findByEmploye(employeId);
    }

    /**
     * Récupère toutes les fiches de paie pour une période donnée
     */
    public List<FichePaie> findByPeriode(int mois, int annee) {
        return fichePaieRepository.findByPeriode(mois, annee);
    }

    /**
     * Récupère toutes les fiches de paie
     */
    public List<FichePaie> findAll() {
        return fichePaieRepository.findAll();
    }
}

