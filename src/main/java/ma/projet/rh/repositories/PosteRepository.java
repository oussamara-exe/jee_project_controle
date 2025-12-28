package ma.projet.rh.repositories;

import ma.projet.rh.entities.Poste;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Poste
 */
@Stateless
public class PosteRepository extends GenericRepository<Poste, Long> {

    public PosteRepository() {
        super(Poste.class);
    }

    /**
     * Recherche un poste par son titre
     */
    public Optional<Poste> findByTitre(String titre) {
        TypedQuery<Poste> query = entityManager.createNamedQuery("Poste.findByTitre", Poste.class);
        query.setParameter("titre", titre);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retourne tous les postes triés par titre
     */
    @Override
    public List<Poste> findAll() {
        return entityManager.createNamedQuery("Poste.findAll", Poste.class).getResultList();
    }

    /**
     * Vérifie si un titre de poste existe déjà
     */
    public boolean existsByTitre(String titre) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(p) FROM Poste p WHERE p.titre = :titre", Long.class
        )
        .setParameter("titre", titre)
        .getSingleResult();
        return count > 0;
    }

    /**
     * Retourne le nombre d'employés par poste
     */
    public long countEmployes(Long posteId) {
        return entityManager.createQuery(
            "SELECT COUNT(e) FROM Employe e WHERE e.poste.id = :posteId AND e.actif = true", Long.class
        )
        .setParameter("posteId", posteId)
        .getSingleResult();
    }

    /**
     * Recherche un poste par ID (les postes n'ont pas de relations LAZY, donc findById standard suffit)
     */
    // Pas besoin de findByIdWithRelations car Poste n'a pas de relations LAZY
}

