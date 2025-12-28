package ma.projet.rh.entities;

import ma.projet.rh.enums.StatutFichePaie;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une fiche de paie mensuelle
 * Contient tous les calculs de salaire
 */
@Entity
@Table(name = "fiches_paie",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "mois", "annee"}),
       indexes = {
           @Index(name = "idx_paie_employe", columnList = "employe_id"),
           @Index(name = "idx_paie_periode", columnList = "mois, annee"),
           @Index(name = "idx_paie_statut", columnList = "statut")
       })
@NamedQueries({
    @NamedQuery(name = "FichePaie.findAll", query = "SELECT f FROM FichePaie f ORDER BY f.annee DESC, f.mois DESC"),
    @NamedQuery(name = "FichePaie.findByEmploye", query = "SELECT f FROM FichePaie f WHERE f.employe.id = :employeId ORDER BY f.annee DESC, f.mois DESC"),
    @NamedQuery(name = "FichePaie.findByPeriode", query = "SELECT f FROM FichePaie f WHERE f.mois = :mois AND f.annee = :annee ORDER BY f.employe.nom, f.employe.prenom"),
    @NamedQuery(name = "FichePaie.findByStatut", query = "SELECT f FROM FichePaie f WHERE f.statut = :statut ORDER BY f.annee DESC, f.mois DESC")
})
public class FichePaie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Column(nullable = false)
    private Integer mois; // 1-12

    @Column(nullable = false)
    private Integer annee;

    // Composantes du salaire
    @Column(name = "salaire_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salaireBase;

    @Column(name = "heures_supplementaires", precision = 6, scale = 2)
    private BigDecimal heuresSupplementaires = BigDecimal.ZERO;

    @Column(name = "montant_heures_sup", precision = 10, scale = 2)
    private BigDecimal montantHeuresSup = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal primes = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal indemnites = BigDecimal.ZERO;

    @Column(name = "prime_anciennete", precision = 10, scale = 2)
    private BigDecimal primeAnciennete = BigDecimal.ZERO;

    // Total brut
    @Column(name = "total_brut", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalBrut;

    // Déductions
    @Column(name = "cotisations_sociales", precision = 10, scale = 2)
    private BigDecimal cotisationsSociales = BigDecimal.ZERO;

    @Column(name = "retenue_ir", precision = 10, scale = 2)
    private BigDecimal retenueIR = BigDecimal.ZERO;

    @Column(name = "autres_deductions", precision = 10, scale = 2)
    private BigDecimal autresDeductions = BigDecimal.ZERO;

    // Total déductions
    @Column(name = "total_deductions", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDeductions;

    // Net à payer
    @Column(name = "net_a_payer", nullable = false, precision = 10, scale = 2)
    private BigDecimal netAPayer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutFichePaie statut = StatutFichePaie.CALCULE;

    @Column(name = "date_generation", nullable = false)
    private LocalDateTime dateGeneration;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_id")
    private UserAccount validateur;

    @Column(name = "fichier_pdf", length = 255)
    private String fichierPdf;

    @Version
    private Long version; // Pour l'optimistic locking

    // Constructeurs
    public FichePaie() {
        this.dateGeneration = LocalDateTime.now();
    }

    public FichePaie(Employe employe, Integer mois, Integer annee) {
        this();
        this.employe = employe;
        this.mois = mois;
        this.annee = annee;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Integer getMois() {
        return mois;
    }

    public void setMois(Integer mois) {
        this.mois = mois;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public BigDecimal getSalaireBase() {
        return salaireBase;
    }

    public void setSalaireBase(BigDecimal salaireBase) {
        this.salaireBase = salaireBase;
    }

    public BigDecimal getHeuresSupplementaires() {
        return heuresSupplementaires;
    }

    public void setHeuresSupplementaires(BigDecimal heuresSupplementaires) {
        this.heuresSupplementaires = heuresSupplementaires;
    }

    public BigDecimal getMontantHeuresSup() {
        return montantHeuresSup;
    }

    public void setMontantHeuresSup(BigDecimal montantHeuresSup) {
        this.montantHeuresSup = montantHeuresSup;
    }

    public BigDecimal getPrimes() {
        return primes;
    }

    public void setPrimes(BigDecimal primes) {
        this.primes = primes;
    }

    public BigDecimal getIndemnites() {
        return indemnites;
    }

    public void setIndemnites(BigDecimal indemnites) {
        this.indemnites = indemnites;
    }

    public BigDecimal getPrimeAnciennete() {
        return primeAnciennete;
    }

    public void setPrimeAnciennete(BigDecimal primeAnciennete) {
        this.primeAnciennete = primeAnciennete;
    }

    public BigDecimal getTotalBrut() {
        return totalBrut;
    }

    public void setTotalBrut(BigDecimal totalBrut) {
        this.totalBrut = totalBrut;
    }

    public BigDecimal getCotisationsSociales() {
        return cotisationsSociales;
    }

    public void setCotisationsSociales(BigDecimal cotisationsSociales) {
        this.cotisationsSociales = cotisationsSociales;
    }

    public BigDecimal getRetenueIR() {
        return retenueIR;
    }

    public void setRetenueIR(BigDecimal retenueIR) {
        this.retenueIR = retenueIR;
    }

    public BigDecimal getAutresDeductions() {
        return autresDeductions;
    }

    public void setAutresDeductions(BigDecimal autresDeductions) {
        this.autresDeductions = autresDeductions;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(BigDecimal totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public BigDecimal getNetAPayer() {
        return netAPayer;
    }

    public void setNetAPayer(BigDecimal netAPayer) {
        this.netAPayer = netAPayer;
    }

    public StatutFichePaie getStatut() {
        return statut;
    }

    public void setStatut(StatutFichePaie statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public UserAccount getValidateur() {
        return validateur;
    }

    public void setValidateur(UserAccount validateur) {
        this.validateur = validateur;
    }

    public String getFichierPdf() {
        return fichierPdf;
    }

    public void setFichierPdf(String fichierPdf) {
        this.fichierPdf = fichierPdf;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Méthodes utilitaires
    /**
     * Calcule le salaire brut total
     */
    public void calculerTotalBrut() {
        this.totalBrut = salaireBase
            .add(montantHeuresSup != null ? montantHeuresSup : BigDecimal.ZERO)
            .add(primes != null ? primes : BigDecimal.ZERO)
            .add(indemnites != null ? indemnites : BigDecimal.ZERO)
            .add(primeAnciennete != null ? primeAnciennete : BigDecimal.ZERO);
    }

    /**
     * Calcule le total des déductions
     */
    public void calculerTotalDeductions() {
        this.totalDeductions = (cotisationsSociales != null ? cotisationsSociales : BigDecimal.ZERO)
            .add(retenueIR != null ? retenueIR : BigDecimal.ZERO)
            .add(autresDeductions != null ? autresDeductions : BigDecimal.ZERO);
    }

    /**
     * Calcule le net à payer
     */
    public void calculerNetAPayer() {
        calculerTotalBrut();
        calculerTotalDeductions();
        this.netAPayer = totalBrut.subtract(totalDeductions);
    }

    /**
     * Valide la fiche de paie
     */
    public void valider(UserAccount validateur) {
        this.statut = StatutFichePaie.VALIDE;
        this.validateur = validateur;
        this.dateValidation = LocalDateTime.now();
    }

    /**
     * Marque comme payé
     */
    public void marquerCommePaye(LocalDate datePaiement) {
        this.statut = StatutFichePaie.PAYE;
        this.datePaiement = datePaiement;
    }

    /**
     * Retourne la période au format "Mois Année"
     */
    public String getPeriodeLibelle() {
        String[] moisNoms = {"", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        return moisNoms[mois] + " " + annee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FichePaie)) return false;
        FichePaie that = (FichePaie) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FichePaie{" +
                "id=" + id +
                ", mois=" + mois +
                ", annee=" + annee +
                ", totalBrut=" + totalBrut +
                ", netAPayer=" + netAPayer +
                ", statut=" + statut +
                '}';
    }
}

