package ma.projet.rh.repositories;

import ma.projet.rh.entities.SoldeConge;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité SoldeConge
 */
@Stateless
public class SoldeCongeRepository extends GenericRepository<SoldeConge, Long> {

    public SoldeCongeRepository() {
        super(SoldeConge.class);
    }

    /**
     * Retourne les soldes d'un employé
     */
    public List<SoldeConge> findByEmploye(Long employeId) {
        TypedQuery<SoldeConge> query = entityManager.createNamedQuery("SoldeConge.findByEmploye", SoldeConge.class);
        query.setParameter("employeId", employeId);
        return query.getResultList();
    }

    /**
     * Retourne le solde d'un employé pour une année donnée
     */
    public Optional<SoldeConge> findByEmployeAnnee(Long employeId, Integer annee) {
        TypedQuery<SoldeConge> query = entityManager.createNamedQuery("SoldeConge.findByEmployeAnnee", SoldeConge.class);
        query.setParameter("employeId", employeId);
        query.setParameter("annee", annee);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retourne tous les soldes pour une année
     */
    public List<SoldeConge> findByAnnee(Integer annee) {
        TypedQuery<SoldeConge> query = entityManager.createNamedQuery("SoldeConge.findByAnnee", SoldeConge.class);
        query.setParameter("annee", annee);
        return query.getResultList();
    }
}

