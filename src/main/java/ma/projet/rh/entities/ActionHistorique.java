package ma.projet.rh.entities;

import ma.projet.rh.enums.TypeAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité représentant une action enregistrée dans l'historique
 * Utilisée pour la traçabilité et l'audit
 */
@Entity
@Table(name = "actions_historique", indexes = {
    @Index(name = "idx_hist_utilisateur", columnList = "utilisateur_id"),
    @Index(name = "idx_hist_type", columnList = "type_action"),
    @Index(name = "idx_hist_date", columnList = "date_action"),
    @Index(name = "idx_hist_entite", columnList = "entite_type, entite_id")
})
@NamedQueries({
    @NamedQuery(name = "ActionHistorique.findAll", query = "SELECT a FROM ActionHistorique a ORDER BY a.dateAction DESC"),
    @NamedQuery(name = "ActionHistorique.findByUtilisateur", query = "SELECT a FROM ActionHistorique a WHERE a.utilisateur.id = :utilisateurId ORDER BY a.dateAction DESC"),
    @NamedQuery(name = "ActionHistorique.findByType", query = "SELECT a FROM ActionHistorique a WHERE a.typeAction = :typeAction ORDER BY a.dateAction DESC"),
    @NamedQuery(name = "ActionHistorique.findByEntite", query = "SELECT a FROM ActionHistorique a WHERE a.entiteType = :entiteType AND a.entiteId = :entiteId ORDER BY a.dateAction DESC"),
    @NamedQuery(name = "ActionHistorique.findByPeriode", query = "SELECT a FROM ActionHistorique a WHERE a.dateAction >= :dateDebut AND a.dateAction <= :dateFin ORDER BY a.dateAction DESC")
})
public class ActionHistorique implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private UserAccount utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_action", nullable = false, length = 30)
    private TypeAction typeAction;

    @Column(name = "entite_type", nullable = false, length = 50)
    private String entiteType; // Ex: "Employe", "Conge", "FichePaie"

    @Column(name = "entite_id")
    private Long entiteId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "valeurs_avant", columnDefinition = "TEXT")
    private String valeursAvant; // JSON ou texte structuré

    @Column(name = "valeurs_apres", columnDefinition = "TEXT")
    private String valeursApres; // JSON ou texte structuré

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;

    @Column(name = "adresse_ip", length = 45) // IPv6 max length
    private String adresseIP;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    // Constructeurs
    public ActionHistorique() {
        this.dateAction = LocalDateTime.now();
    }

    public ActionHistorique(UserAccount utilisateur, TypeAction typeAction, String entiteType, Long entiteId, String description) {
        this();
        this.utilisateur = utilisateur;
        this.typeAction = typeAction;
        this.entiteType = entiteType;
        this.entiteId = entiteId;
        this.description = description;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserAccount utilisateur) {
        this.utilisateur = utilisateur;
    }

    public TypeAction getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(TypeAction typeAction) {
        this.typeAction = typeAction;
    }

    public String getEntiteType() {
        return entiteType;
    }

    public void setEntiteType(String entiteType) {
        this.entiteType = entiteType;
    }

    public Long getEntiteId() {
        return entiteId;
    }

    public void setEntiteId(Long entiteId) {
        this.entiteId = entiteId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValeursAvant() {
        return valeursAvant;
    }

    public void setValeursAvant(String valeursAvant) {
        this.valeursAvant = valeursAvant;
    }

    public String getValeursApres() {
        return valeursApres;
    }

    public void setValeursApres(String valeursApres) {
        this.valeursApres = valeursApres;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }

    public String getAdresseIP() {
        return adresseIP;
    }

    public void setAdresseIP(String adresseIP) {
        this.adresseIP = adresseIP;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    // Méthodes utilitaires
    /**
     * Retourne une description complète de l'action
     */
    public String getDescriptionComplete() {
        StringBuilder sb = new StringBuilder();
        sb.append(typeAction.getLibelle()).append(" - ");
        sb.append(description);
        
        if (entiteType != null) {
            sb.append(" [").append(entiteType);
            if (entiteId != null) {
                sb.append(" #").append(entiteId);
            }
            sb.append("]");
        }
        
        return sb.toString();
    }

    /**
     * Vérifie si l'action a des changements enregistrés
     */
    public boolean hasChanges() {
        return valeursAvant != null && valeursApres != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionHistorique)) return false;
        ActionHistorique that = (ActionHistorique) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ActionHistorique{" +
                "id=" + id +
                ", typeAction=" + typeAction +
                ", entiteType='" + entiteType + '\'' +
                ", entiteId=" + entiteId +
                ", dateAction=" + dateAction +
                '}';
    }
}

