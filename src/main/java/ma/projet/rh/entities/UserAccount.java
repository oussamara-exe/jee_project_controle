package ma.projet.rh.entities;

import ma.projet.rh.enums.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un compte utilisateur pour l'authentification
 */
@Entity
@Table(name = "user_accounts")
@NamedQueries({
    @NamedQuery(name = "UserAccount.findAll", query = "SELECT u FROM UserAccount u ORDER BY u.username"),
    @NamedQuery(name = "UserAccount.findByUsername", query = "SELECT u FROM UserAccount u WHERE u.username = :username"),
    @NamedQuery(name = "UserAccount.findActive", query = "SELECT u FROM UserAccount u WHERE u.actif = true ORDER BY u.username")
})
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password; // Mot de passe hashé avec BCrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    @Column(name = "tentatives_connexion_echouees")
    private Integer tentativesConnexionEchouees = 0;

    @Column(name = "date_verrouillage")
    private LocalDateTime dateVerrouillage;

    @OneToOne(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private Employe employe;

    @OneToMany(mappedBy = "utilisateur", fetch = FetchType.LAZY)
    private List<ActionHistorique> actions = new ArrayList<>();

    @OneToMany(mappedBy = "destinataire", fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    // Constructeurs
    public UserAccount() {
        this.dateCreation = LocalDateTime.now();
    }

    public UserAccount(String username, String password, Role role) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    public Integer getTentativesConnexionEchouees() {
        return tentativesConnexionEchouees;
    }

    public void setTentativesConnexionEchouees(Integer tentativesConnexionEchouees) {
        this.tentativesConnexionEchouees = tentativesConnexionEchouees;
    }

    public LocalDateTime getDateVerrouillage() {
        return dateVerrouillage;
    }

    public void setDateVerrouillage(LocalDateTime dateVerrouillage) {
        this.dateVerrouillage = dateVerrouillage;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public List<ActionHistorique> getActions() {
        return actions;
    }

    public void setActions(List<ActionHistorique> actions) {
        this.actions = actions;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    // Méthodes utilitaires
    public boolean isVerrouille() {
        return dateVerrouillage != null && 
               dateVerrouillage.plusHours(24).isAfter(LocalDateTime.now());
    }

    public void incrementerTentativesEchouees() {
        this.tentativesConnexionEchouees++;
        if (this.tentativesConnexionEchouees >= 5) {
            this.dateVerrouillage = LocalDateTime.now();
        }
    }

    public void resetTentativesEchouees() {
        this.tentativesConnexionEchouees = 0;
        this.dateVerrouillage = null;
    }

    public void enregistrerConnexion() {
        this.derniereConnexion = LocalDateTime.now();
        resetTentativesEchouees();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount)) return false;
        UserAccount that = (UserAccount) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", actif=" + actif +
                '}';
    }
}

