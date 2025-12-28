package ma.projet.rh.entities;

import ma.projet.rh.enums.StatutConge;
import ma.projet.rh.enums.TypeConge;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entité représentant une demande de congé
 * Implémente un workflow de validation : Employé -> Manager -> RH
 */
@Entity
@Table(name = "conges", indexes = {
    @Index(name = "idx_conge_employe", columnList = "employe_id"),
    @Index(name = "idx_conge_statut", columnList = "statut"),
    @Index(name = "idx_conge_dates", columnList = "date_debut, date_fin")
})
@NamedQueries({
    @NamedQuery(name = "Conge.findAll", query = "SELECT c FROM Conge c ORDER BY c.dateDemande DESC"),
    @NamedQuery(name = "Conge.findByEmploye", query = "SELECT c FROM Conge c WHERE c.employe.id = :employeId ORDER BY c.dateDemande DESC"),
    @NamedQuery(name = "Conge.findByStatut", query = "SELECT c FROM Conge c WHERE c.statut = :statut ORDER BY c.dateDemande"),
    @NamedQuery(name = "Conge.findEnAttenteManager", query = "SELECT c FROM Conge c WHERE c.statut = ma.projet.rh.enums.StatutConge.EN_ATTENTE AND c.employe.manager.id = :managerId ORDER BY c.dateDemande"),
    @NamedQuery(name = "Conge.findEnAttenteRH", query = "SELECT c FROM Conge c WHERE c.statut = ma.projet.rh.enums.StatutConge.VALIDE_MANAGER ORDER BY c.dateDemande"),
    @NamedQuery(name = "Conge.findByPeriode", query = "SELECT c FROM Conge c WHERE c.dateDebut <= :dateFin AND c.dateFin >= :dateDebut AND c.statut = ma.projet.rh.enums.StatutConge.APPROUVE")
})
public class Conge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_conge", nullable = false, length = 20)
    private TypeConge typeConge;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "nombre_jours", nullable = false)
    private Integer nombreJours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutConge statut = StatutConge.EN_ATTENTE;

    @Column(name = "commentaire_employe", columnDefinition = "TEXT")
    private String commentaireEmploye;

    @Column(name = "commentaire_manager", columnDefinition = "TEXT")
    private String commentaireManager;

    @Column(name = "commentaire_rh", columnDefinition = "TEXT")
    private String commentaireRH;

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_validation_manager")
    private LocalDateTime dateValidationManager;

    @Column(name = "date_validation_rh")
    private LocalDateTime dateValidationRH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_manager_id")
    private UserAccount validateurManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_rh_id")
    private UserAccount validateurRH;

    @Version
    private Long version; // Pour l'optimistic locking

    // Constructeurs
    public Conge() {
        this.dateDemande = LocalDateTime.now();
    }

    public Conge(Employe employe, TypeConge typeConge, LocalDate dateDebut, LocalDate dateFin) {
        this();
        this.employe = employe;
        this.typeConge = typeConge;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nombreJours = calculerNombreJours(dateDebut, dateFin);
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

    public TypeConge getTypeConge() {
        return typeConge;
    }

    public void setTypeConge(TypeConge typeConge) {
        this.typeConge = typeConge;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
        if (dateFin != null) {
            this.nombreJours = calculerNombreJours(dateDebut, dateFin);
        }
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
        if (dateDebut != null) {
            this.nombreJours = calculerNombreJours(dateDebut, dateFin);
        }
    }

    public Integer getNombreJours() {
        return nombreJours;
    }

    public void setNombreJours(Integer nombreJours) {
        this.nombreJours = nombreJours;
    }

    public StatutConge getStatut() {
        return statut;
    }

    public void setStatut(StatutConge statut) {
        this.statut = statut;
    }

    public String getCommentaireEmploye() {
        return commentaireEmploye;
    }

    public void setCommentaireEmploye(String commentaireEmploye) {
        this.commentaireEmploye = commentaireEmploye;
    }

    public String getCommentaireManager() {
        return commentaireManager;
    }

    public void setCommentaireManager(String commentaireManager) {
        this.commentaireManager = commentaireManager;
    }

    public String getCommentaireRH() {
        return commentaireRH;
    }

    public void setCommentaireRH(String commentaireRH) {
        this.commentaireRH = commentaireRH;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public LocalDateTime getDateValidationManager() {
        return dateValidationManager;
    }

    public void setDateValidationManager(LocalDateTime dateValidationManager) {
        this.dateValidationManager = dateValidationManager;
    }

    public LocalDateTime getDateValidationRH() {
        return dateValidationRH;
    }

    public void setDateValidationRH(LocalDateTime dateValidationRH) {
        this.dateValidationRH = dateValidationRH;
    }

    public UserAccount getValidateurManager() {
        return validateurManager;
    }

    public void setValidateurManager(UserAccount validateurManager) {
        this.validateurManager = validateurManager;
    }

    public UserAccount getValidateurRH() {
        return validateurRH;
    }

    public void setValidateurRH(UserAccount validateurRH) {
        this.validateurRH = validateurRH;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Méthodes utilitaires
    /**
     * Calcule le nombre de jours ouvrés entre deux dates (exclut weekends)
     */
    private static int calculerNombreJours(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) return 0;
        
        long totalJours = ChronoUnit.DAYS.between(debut, fin) + 1;
        int joursOuvres = 0;
        
        LocalDate current = debut;
        while (!current.isAfter(fin)) {
            // Exclure samedi (6) et dimanche (7)
            if (current.getDayOfWeek().getValue() < 6) {
                joursOuvres++;
            }
            current = current.plusDays(1);
        }
        
        return joursOuvres;
    }

    public boolean isPasse() {
        return dateFin != null && dateFin.isBefore(LocalDate.now());
    }

    public boolean isEnCours() {
        LocalDate now = LocalDate.now();
        return dateDebut != null && dateFin != null && 
               !dateDebut.isAfter(now) && !dateFin.isBefore(now) &&
               statut == StatutConge.APPROUVE;
    }

    public boolean isFutur() {
        return dateDebut != null && dateDebut.isAfter(LocalDate.now());
    }

    /**
     * Valide la demande par le manager
     */
    public void validerParManager(UserAccount manager, String commentaire) {
        this.statut = StatutConge.VALIDE_MANAGER;
        this.validateurManager = manager;
        this.dateValidationManager = LocalDateTime.now();
        this.commentaireManager = commentaire;
    }

    /**
     * Valide la demande par les RH (validation finale)
     */
    public void validerParRH(UserAccount rh, String commentaire) {
        this.statut = StatutConge.APPROUVE;
        this.validateurRH = rh;
        this.dateValidationRH = LocalDateTime.now();
        this.commentaireRH = commentaire;
    }

    /**
     * Refuse la demande
     */
    public void refuser(String commentaire, boolean parManager) {
        this.statut = StatutConge.REFUSE;
        if (parManager) {
            this.commentaireManager = commentaire;
            this.dateValidationManager = LocalDateTime.now();
        } else {
            this.commentaireRH = commentaire;
            this.dateValidationRH = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conge)) return false;
        Conge conge = (Conge) o;
        return id != null && id.equals(conge.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Conge{" +
                "id=" + id +
                ", typeConge=" + typeConge +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", nombreJours=" + nombreJours +
                ", statut=" + statut +
                '}';
    }
}

