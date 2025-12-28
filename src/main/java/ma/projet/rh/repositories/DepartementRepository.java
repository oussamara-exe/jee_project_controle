package ma.projet.rh.repositories;

import ma.projet.rh.entities.Departement;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Departement
 */
@Stateless
public class DepartementRepository extends GenericRepository<Departement, Long> {

    public DepartementRepository() {
        super(Departement.class);
    }

    /**
     * Recherche un département par son nom
     */
    public Optional<Departement> findByNom(String nom) {
        TypedQuery<Departement> query = entityManager.createNamedQuery("Departement.findByNom", Departement.class);
        query.setParameter("nom", nom);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retourne tous les départements triés par nom avec leurs responsables chargés
     */
    @Override
    public List<Departement> findAll() {
        TypedQuery<Departement> query = entityManager.createQuery(
            "SELECT DISTINCT d FROM Departement d " +
            "LEFT JOIN FETCH d.responsable " +
            "ORDER BY d.nom", 
            Departement.class
        );
        return query.getResultList();
    }

    /**
     * Recherche un département par ID avec ses relations chargées
     */
    public Optional<Departement> findByIdWithRelations(Long id) {
        try {
            TypedQuery<Departement> query = entityManager.createQuery(
                "SELECT DISTINCT d FROM Departement d " +
                "LEFT JOIN FETCH d.responsable " +
                "WHERE d.id = :id", 
                Departement.class
            );
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Vérifie si un nom de département existe déjà
     */
    public boolean existsByNom(String nom) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(d) FROM Departement d WHERE d.nom = :nom", Long.class
        )
        .setParameter("nom", nom)
        .getSingleResult();
        return count > 0;
    }

    /**
     * Retourne le nombre d'employés par département
     */
    public long countEmployes(Long departementId) {
        return entityManager.createQuery(
            "SELECT COUNT(e) FROM Employe e WHERE e.departement.id = :departementId AND e.actif = true", Long.class
        )
        .setParameter("departementId", departementId)
        .getSingleResult();
    }
}

