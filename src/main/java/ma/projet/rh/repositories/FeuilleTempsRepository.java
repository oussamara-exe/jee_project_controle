package ma.projet.rh.repositories;

import ma.projet.rh.entities.FeuilleTemps;
import ma.projet.rh.enums.StatutFeuilleTemps;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité FeuilleTemps
 */
@Stateless
public class FeuilleTempsRepository extends GenericRepository<FeuilleTemps, Long> {

    public FeuilleTempsRepository() {
        super(FeuilleTemps.class);
    }

    public List<FeuilleTemps> findByEmploye(Long employeId) {
        TypedQuery<FeuilleTemps> query = entityManager.createQuery(
            "SELECT DISTINCT f FROM FeuilleTemps f LEFT JOIN FETCH f.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE f.employe.id = :employeId ORDER BY f.dateSemaine DESC", 
            FeuilleTemps.class
        );
        query.setParameter("employeId", employeId);
        return query.getResultList();
    }

    public List<FeuilleTemps> findByStatut(StatutFeuilleTemps statut) {
        TypedQuery<FeuilleTemps> query = entityManager.createQuery(
            "SELECT DISTINCT f FROM FeuilleTemps f LEFT JOIN FETCH f.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE f.statut = :statut ORDER BY f.dateSemaine DESC", 
            FeuilleTemps.class
        );
        query.setParameter("statut", statut);
        return query.getResultList();
    }

    public List<FeuilleTemps> findEnAttenteValidation(Long managerId) {
        TypedQuery<FeuilleTemps> query = entityManager.createQuery(
            "SELECT DISTINCT f FROM FeuilleTemps f LEFT JOIN FETCH f.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE f.statut = ma.projet.rh.enums.StatutFeuilleTemps.SOUMIS AND e.manager.id = :managerId ORDER BY f.dateSemaine DESC", 
            FeuilleTemps.class
        );
        query.setParameter("managerId", managerId);
        return query.getResultList();
    }

    public List<FeuilleTemps> findByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        TypedQuery<FeuilleTemps> query = entityManager.createNamedQuery("FeuilleTemps.findByPeriode", FeuilleTemps.class);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    /**
     * Vérifie si une feuille de temps existe déjà pour un employé et une date de semaine
     */
    public boolean existsByEmployeAndDateSemaine(Long employeId, LocalDate dateSemaine) {
        try {
            Long count = entityManager.createQuery(
                "SELECT COUNT(f) FROM FeuilleTemps f WHERE f.employe.id = :employeId AND f.dateSemaine = :dateSemaine",
                Long.class
            )
            .setParameter("employeId", employeId)
            .setParameter("dateSemaine", dateSemaine)
            .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Recherche les feuilles de temps d'un employé pour un mois et une année donnés
     */
    public List<FeuilleTemps> findByEmployeAndPeriode(Long employeId, int mois, int annee) {
        LocalDate dateDebut = LocalDate.of(annee, mois, 1);
        LocalDate dateFin = dateDebut.withDayOfMonth(dateDebut.lengthOfMonth());
        
        TypedQuery<FeuilleTemps> query = entityManager.createQuery(
            "SELECT DISTINCT f FROM FeuilleTemps f " +
            "LEFT JOIN FETCH f.employe e " +
            "LEFT JOIN FETCH e.departement " +
            "LEFT JOIN FETCH e.poste " +
            "WHERE f.employe.id = :employeId " +
            "AND f.dateSemaine >= :dateDebut " +
            "AND f.dateSemaine <= :dateFin " +
            "ORDER BY f.dateSemaine",
            FeuilleTemps.class
        );
        query.setParameter("employeId", employeId);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    /**
     * Recherche une feuille de temps par ID avec toutes les relations chargées
     */
    public Optional<FeuilleTemps> findByIdWithRelations(Long id) {
        try {
            TypedQuery<FeuilleTemps> query = entityManager.createQuery(
                "SELECT DISTINCT f FROM FeuilleTemps f " +
                "LEFT JOIN FETCH f.employe e " +
                "LEFT JOIN FETCH e.departement " +
                "LEFT JOIN FETCH e.poste " +
                "LEFT JOIN FETCH e.manager " +
                "WHERE f.id = :id", 
                FeuilleTemps.class
            );
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

