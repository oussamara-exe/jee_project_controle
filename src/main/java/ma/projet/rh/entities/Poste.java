package ma.projet.rh.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un poste dans l'entreprise
 */
@Entity
@Table(name = "postes")
@NamedQueries({
    @NamedQuery(name = "Poste.findAll", query = "SELECT p FROM Poste p ORDER BY p.titre"),
    @NamedQuery(name = "Poste.findByTitre", query = "SELECT p FROM Poste p WHERE p.titre = :titre")
})
public class Poste implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "salaire_min", precision = 10, scale = 2)
    private BigDecimal salaireMin;

    @Column(name = "salaire_max", precision = 10, scale = 2)
    private BigDecimal salaireMax;

    @OneToMany(mappedBy = "poste", fetch = FetchType.LAZY)
    private List<Employe> employes = new ArrayList<>();

    // Constructeurs
    public Poste() {
    }

    public Poste(String titre, String description, BigDecimal salaireMin, BigDecimal salaireMax) {
        this.titre = titre;
        this.description = description;
        this.salaireMin = salaireMin;
        this.salaireMax = salaireMax;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSalaireMin() {
        return salaireMin;
    }

    public void setSalaireMin(BigDecimal salaireMin) {
        this.salaireMin = salaireMin;
    }

    public BigDecimal getSalaireMax() {
        return salaireMax;
    }

    public void setSalaireMax(BigDecimal salaireMax) {
        this.salaireMax = salaireMax;
    }

    public List<Employe> getEmployes() {
        return employes;
    }

    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
    }

    // Méthodes utilitaires
    public int getNombreEmployes() {
        return employes != null ? employes.size() : 0;
    }

    public boolean isSalaireValide(BigDecimal salaire) {
        if (salaire == null) return false;
        boolean minOk = salaireMin == null || salaire.compareTo(salaireMin) >= 0;
        boolean maxOk = salaireMax == null || salaire.compareTo(salaireMax) <= 0;
        return minOk && maxOk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Poste)) return false;
        Poste poste = (Poste) o;
        return id != null && id.equals(poste.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Poste{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", nombreEmployes=" + getNombreEmployes() +
                '}';
    }
}

