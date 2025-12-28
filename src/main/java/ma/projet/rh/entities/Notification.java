package ma.projet.rh.entities;

import ma.projet.rh.enums.TypeNotification;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité représentant une notification pour un utilisateur
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notif_destinataire", columnList = "destinataire_id"),
    @Index(name = "idx_notif_lu", columnList = "lu"),
    @Index(name = "idx_notif_date", columnList = "date_creation")
})
@NamedQueries({
    @NamedQuery(name = "Notification.findAll", query = "SELECT n FROM Notification n ORDER BY n.dateCreation DESC"),
    @NamedQuery(name = "Notification.findByDestinataire", query = "SELECT n FROM Notification n WHERE n.destinataire.id = :destinataireId ORDER BY n.dateCreation DESC"),
    @NamedQuery(name = "Notification.findNonLues", query = "SELECT n FROM Notification n WHERE n.destinataire.id = :destinataireId AND n.lu = false ORDER BY n.dateCreation DESC"),
    @NamedQuery(name = "Notification.countNonLues", query = "SELECT COUNT(n) FROM Notification n WHERE n.destinataire.id = :destinataireId AND n.lu = false")
})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private UserAccount destinataire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeNotification type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private Boolean lu = false;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @Column(length = 255)
    private String lien; // URL optionnelle vers l'élément concerné

    @Column(name = "entite_type", length = 50)
    private String entiteType; // Ex: "Conge", "FichePaie"

    @Column(name = "entite_id")
    private Long entiteId;

    // Constructeurs
    public Notification() {
        this.dateCreation = LocalDateTime.now();
    }

    public Notification(UserAccount destinataire, TypeNotification type, String message) {
        this();
        this.destinataire = destinataire;
        this.type = type;
        this.message = message;
    }

    public Notification(UserAccount destinataire, TypeNotification type, String message, String lien) {
        this(destinataire, type, message);
        this.lien = lien;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(UserAccount destinataire) {
        this.destinataire = destinataire;
    }

    public TypeNotification getType() {
        return type;
    }

    public void setType(TypeNotification type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getLu() {
        return lu;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
        if (lu && dateLecture == null) {
            this.dateLecture = LocalDateTime.now();
        }
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(LocalDateTime dateLecture) {
        this.dateLecture = dateLecture;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
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

    // Méthodes utilitaires
    /**
     * Marque la notification comme lue
     */
    public void marquerCommeLue() {
        this.lu = true;
        this.dateLecture = LocalDateTime.now();
    }

    /**
     * Vérifie si la notification est récente (moins de 24h)
     */
    public boolean isRecente() {
        return dateCreation.plusHours(24).isAfter(LocalDateTime.now());
    }

    /**
     * Retourne la classe CSS pour le badge selon le type
     */
    public String getCssClass() {
        return type != null ? type.getCssClass() : "info";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", type=" + type +
                ", lu=" + lu +
                ", dateCreation=" + dateCreation +
                '}';
    }
}

