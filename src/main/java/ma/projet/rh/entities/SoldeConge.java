package ma.projet.rh.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Entité représentant le solde de congés d'un employé pour une année donnée
 */
@Entity
@Table(name = "soldes_conge", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "annee"}),
       indexes = {
           @Index(name = "idx_solde_employe", columnList = "employe_id"),
           @Index(name = "idx_solde_annee", columnList = "annee")
       })
@NamedQueries({
    @NamedQuery(name = "SoldeConge.findByEmploye", query = "SELECT s FROM SoldeConge s WHERE s.employe.id = :employeId ORDER BY s.annee DESC"),
    @NamedQuery(name = "SoldeConge.findByEmployeAnnee", query = "SELECT s FROM SoldeConge s WHERE s.employe.id = :employeId AND s.annee = :annee"),
    @NamedQuery(name = "SoldeConge.findByAnnee", query = "SELECT s FROM SoldeConge s WHERE s.annee = :annee")
})
public class SoldeConge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Column(nullable = false)
    private Integer annee;

    @Column(name = "jours_acquis", nullable = false)
    private Integer joursAcquis = 0;

    @Column(name = "jours_pris", nullable = false)
    private Integer joursPris = 0;

    @Column(name = "jours_restants", nullable = false)
    private Integer joursRestants = 0;

    // Constructeurs
    public SoldeConge() {
    }

    public SoldeConge(Employe employe, Integer annee, Integer joursAcquis) {
        this.employe = employe;
        this.annee = annee;
        this.joursAcquis = joursAcquis;
        this.joursRestants = joursAcquis;
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

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Integer getJoursAcquis() {
        return joursAcquis;
    }

    public void setJoursAcquis(Integer joursAcquis) {
        this.joursAcquis = joursAcquis;
        recalculerJoursRestants();
    }

    public Integer getJoursPris() {
        return joursPris;
    }

    public void setJoursPris(Integer joursPris) {
        this.joursPris = joursPris;
        recalculerJoursRestants();
    }

    public Integer getJoursRestants() {
        return joursRestants;
    }

    public void setJoursRestants(Integer joursRestants) {
        this.joursRestants = joursRestants;
    }

    // Méthodes utilitaires
    private void recalculerJoursRestants() {
        this.joursRestants = this.joursAcquis - this.joursPris;
    }

    /**
     * Déduire des jours du solde (lors de l'approbation d'un congé)
     */
    public boolean deduireJours(int jours) {
        if (jours > joursRestants) {
            return false; // Pas assez de jours
        }
        this.joursPris += jours;
        recalculerJoursRestants();
        return true;
    }

    /**
     * Restituer des jours au solde (lors de l'annulation d'un congé)
     */
    public void restituerJours(int jours) {
        this.joursPris = Math.max(0, this.joursPris - jours);
        recalculerJoursRestants();
    }

    /**
     * Ajouter des jours acquis
     */
    public void ajouterJoursAcquis(int jours) {
        this.joursAcquis += jours;
        recalculerJoursRestants();
    }

    /**
     * Vérifie si le solde est suffisant pour un nombre de jours
     */
    public boolean isSuffisant(int joursNecessaires) {
        return joursRestants >= joursNecessaires;
    }

    /**
     * Retourne le pourcentage de congés utilisés
     */
    public double getPourcentageUtilise() {
        if (joursAcquis == 0) return 0;
        return (joursPris * 100.0) / joursAcquis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SoldeConge)) return false;
        SoldeConge that = (SoldeConge) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SoldeConge{" +
                "id=" + id +
                ", annee=" + annee +
                ", joursAcquis=" + joursAcquis +
                ", joursPris=" + joursPris +
                ", joursRestants=" + joursRestants +
                '}';
    }
}

