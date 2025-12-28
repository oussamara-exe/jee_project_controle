package ma.projet.rh.entities;

import ma.projet.rh.enums.StatutFeuilleTemps;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une feuille de temps hebdomadaire d'un employé
 */
@Entity
@Table(name = "feuilles_temps",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "date_semaine"}),
       indexes = {
           @Index(name = "idx_temps_employe", columnList = "employe_id"),
           @Index(name = "idx_temps_semaine", columnList = "date_semaine"),
           @Index(name = "idx_temps_statut", columnList = "statut")
       })
@NamedQueries({
    @NamedQuery(name = "FeuilleTemps.findAll", query = "SELECT f FROM FeuilleTemps f ORDER BY f.dateSemaine DESC"),
    @NamedQuery(name = "FeuilleTemps.findByEmploye", query = "SELECT f FROM FeuilleTemps f WHERE f.employe.id = :employeId ORDER BY f.dateSemaine DESC"),
    @NamedQuery(name = "FeuilleTemps.findByStatut", query = "SELECT f FROM FeuilleTemps f WHERE f.statut = :statut ORDER BY f.dateSemaine"),
    @NamedQuery(name = "FeuilleTemps.findEnAttenteValidation", query = "SELECT f FROM FeuilleTemps f WHERE f.statut = ma.projet.rh.enums.StatutFeuilleTemps.SOUMIS AND f.employe.manager.id = :managerId ORDER BY f.dateSemaine"),
    @NamedQuery(name = "FeuilleTemps.findByPeriode", query = "SELECT f FROM FeuilleTemps f WHERE f.dateSemaine >= :dateDebut AND f.dateSemaine <= :dateFin ORDER BY f.dateSemaine")
})
public class FeuilleTemps implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Column(name = "date_semaine", nullable = false)
    private LocalDate dateSemaine; // Premier jour de la semaine (lundi)

    @Column(name = "heures_normales", nullable = false, precision = 5, scale = 2)
    private BigDecimal heuresNormales = BigDecimal.ZERO;

    @Column(name = "heures_supplementaires", precision = 5, scale = 2)
    private BigDecimal heuresSupplementaires = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutFeuilleTemps statut = StatutFeuilleTemps.BROUILLON;

    @Column(name = "date_saisie", nullable = false)
    private LocalDateTime dateSaisie;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_id")
    private UserAccount validateur;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Version
    private Long version; // Pour l'optimistic locking

    // Constructeurs
    public FeuilleTemps() {
        this.dateSaisie = LocalDateTime.now();
    }

    public FeuilleTemps(Employe employe, LocalDate dateSemaine) {
        this();
        this.employe = employe;
        this.dateSemaine = dateSemaine;
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

    public LocalDate getDateSemaine() {
        return dateSemaine;
    }

    public void setDateSemaine(LocalDate dateSemaine) {
        this.dateSemaine = dateSemaine;
    }

    public BigDecimal getHeuresNormales() {
        return heuresNormales;
    }

    public void setHeuresNormales(BigDecimal heuresNormales) {
        this.heuresNormales = heuresNormales;
    }

    public BigDecimal getHeuresSupplementaires() {
        return heuresSupplementaires;
    }

    public void setHeuresSupplementaires(BigDecimal heuresSupplementaires) {
        this.heuresSupplementaires = heuresSupplementaires;
    }

    public StatutFeuilleTemps getStatut() {
        return statut;
    }

    public void setStatut(StatutFeuilleTemps statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateSaisie() {
        return dateSaisie;
    }

    public void setDateSaisie(LocalDateTime dateSaisie) {
        this.dateSaisie = dateSaisie;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

    public UserAccount getValidateur() {
        return validateur;
    }

    public void setValidateur(UserAccount validateur) {
        this.validateur = validateur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Méthodes utilitaires
    public BigDecimal getTotalHeures() {
        return heuresNormales.add(heuresSupplementaires != null ? heuresSupplementaires : BigDecimal.ZERO);
    }

    /**
     * Soumet la feuille pour validation
     */
    public void soumettre() {
        if (this.statut == StatutFeuilleTemps.BROUILLON) {
            this.statut = StatutFeuilleTemps.SOUMIS;
        }
    }

    /**
     * Valide la feuille de temps
     */
    public void valider(UserAccount validateur, String commentaire) {
        this.statut = StatutFeuilleTemps.VALIDE;
        this.validateur = validateur;
        this.dateValidation = LocalDateTime.now();
        this.commentaire = commentaire;
    }

    /**
     * Rejette la feuille et la repasse en brouillon
     */
    public void rejeter(String commentaire) {
        this.statut = StatutFeuilleTemps.BROUILLON;
        this.commentaire = commentaire;
    }

    /**
     * Calcule automatiquement les heures supplémentaires
     * basé sur les heures hebdo contractuelles de l'employé
     */
    public void calculerHeuresSupplementaires() {
        if (employe == null) return;
        
        BigDecimal heuresContractuelles = BigDecimal.valueOf(employe.getHeuresHebdo());
        BigDecimal heuresSup = heuresNormales.subtract(heuresContractuelles);
        
        if (heuresSup.compareTo(BigDecimal.ZERO) > 0) {
            this.heuresSupplementaires = heuresSup;
            this.heuresNormales = heuresContractuelles;
        } else {
            this.heuresSupplementaires = BigDecimal.ZERO;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeuilleTemps)) return false;
        FeuilleTemps that = (FeuilleTemps) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FeuilleTemps{" +
                "id=" + id +
                ", dateSemaine=" + dateSemaine +
                ", heuresNormales=" + heuresNormales +
                ", heuresSupplementaires=" + heuresSupplementaires +
                ", statut=" + statut +
                '}';
    }
}

