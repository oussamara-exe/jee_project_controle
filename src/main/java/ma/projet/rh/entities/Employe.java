package ma.projet.rh.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un employé de l'entreprise
 * Implémente l'optimistic locking avec @Version
 */
@Entity
@Table(name = "employes", indexes = {
    @Index(name = "idx_matricule", columnList = "matricule"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_actif", columnList = "actif")
})
@NamedQueries({
    @NamedQuery(name = "Employe.findAll", query = "SELECT e FROM Employe e ORDER BY e.nom, e.prenom"),
    @NamedQuery(name = "Employe.findActifs", query = "SELECT e FROM Employe e WHERE e.actif = true ORDER BY e.nom, e.prenom"),
    @NamedQuery(name = "Employe.findByMatricule", query = "SELECT e FROM Employe e WHERE e.matricule = :matricule"),
    @NamedQuery(name = "Employe.findByEmail", query = "SELECT e FROM Employe e WHERE e.email = :email"),
    @NamedQuery(name = "Employe.findByDepartement", query = "SELECT e FROM Employe e WHERE e.departement.id = :departementId AND e.actif = true ORDER BY e.nom, e.prenom"),
    @NamedQuery(name = "Employe.findByManager", query = "SELECT e FROM Employe e WHERE e.manager.id = :managerId AND e.actif = true ORDER BY e.nom, e.prenom")
})
public class Employe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String matricule;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String telephone;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;

    @Column(name = "salaire_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salaireBase;

    @Column(name = "heures_hebdo", nullable = false)
    private Integer heuresHebdo = 35;

    @Column(length = 34) // Format IBAN
    private String iban;

    @Column(length = 255)
    private String photo;

    @Column(nullable = false)
    private Boolean actif = true;

    @Version
    private Long version; // Pour l'optimistic locking

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "poste_id")
    private Poste poste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employe manager;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_account_id", unique = true)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Employe> subordonnes = new ArrayList<>();

    @OneToMany(mappedBy = "employe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Conge> conges = new ArrayList<>();

    @OneToMany(mappedBy = "employe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FeuilleTemps> feuillesTemps = new ArrayList<>();

    @OneToMany(mappedBy = "employe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FichePaie> fichesPaie = new ArrayList<>();

    @OneToMany(mappedBy = "employe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SoldeConge> soldesConge = new ArrayList<>();

    // Constructeurs
    public Employe() {
    }

    public Employe(String matricule, String nom, String prenom, String email, LocalDate dateEmbauche, BigDecimal salaireBase) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateEmbauche = dateEmbauche;
        this.salaireBase = salaireBase;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public BigDecimal getSalaireBase() {
        return salaireBase;
    }

    public void setSalaireBase(BigDecimal salaireBase) {
        this.salaireBase = salaireBase;
    }

    public Integer getHeuresHebdo() {
        return heuresHebdo;
    }

    public void setHeuresHebdo(Integer heuresHebdo) {
        this.heuresHebdo = heuresHebdo;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public Employe getManager() {
        return manager;
    }

    public void setManager(Employe manager) {
        this.manager = manager;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public List<Employe> getSubordonnes() {
        return subordonnes;
    }

    public void setSubordonnes(List<Employe> subordonnes) {
        this.subordonnes = subordonnes;
    }

    public List<Conge> getConges() {
        return conges;
    }

    public void setConges(List<Conge> conges) {
        this.conges = conges;
    }

    public List<FeuilleTemps> getFeuillesTemps() {
        return feuillesTemps;
    }

    public void setFeuillesTemps(List<FeuilleTemps> feuillesTemps) {
        this.feuillesTemps = feuillesTemps;
    }

    public List<FichePaie> getFichesPaie() {
        return fichesPaie;
    }

    public void setFichesPaie(List<FichePaie> fichesPaie) {
        this.fichesPaie = fichesPaie;
    }

    public List<SoldeConge> getSoldesConge() {
        return soldesConge;
    }

    public void setSoldesConge(List<SoldeConge> soldesConge) {
        this.soldesConge = soldesConge;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public int getAge() {
        if (dateNaissance == null) return 0;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public int getAnciennete() {
        if (dateEmbauche == null) return 0;
        return Period.between(dateEmbauche, LocalDate.now()).getYears();
    }

    public boolean isManager() {
        return subordonnes != null && !subordonnes.isEmpty();
    }

    public int getNombreSubordonnes() {
        return subordonnes != null ? subordonnes.size() : 0;
    }

    /**
     * Calcule la prime d'ancienneté (2% par année d'ancienneté, max 20%)
     */
    public BigDecimal getPrimeAnciennete() {
        int anciennete = getAnciennete();
        if (anciennete == 0) return BigDecimal.ZERO;
        
        double pourcentage = Math.min(anciennete * 2, 20);
        return salaireBase.multiply(BigDecimal.valueOf(pourcentage / 100));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employe)) return false;
        Employe employe = (Employe) o;
        return id != null && id.equals(employe.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Employe{" +
                "id=" + id +
                ", matricule='" + matricule + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", actif=" + actif +
                '}';
    }
}

