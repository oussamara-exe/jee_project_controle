package ma.projet.rh.repositories;

import ma.projet.rh.entities.FichePaie;
import ma.projet.rh.enums.StatutFichePaie;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité FichePaie
 */
@Stateless
public class FichePaieRepository extends GenericRepository<FichePaie, Long> {

    public FichePaieRepository() {
        super(FichePaie.class);
    }

    public List<FichePaie> findByEmploye(Long employeId) {
        TypedQuery<FichePaie> query = entityManager.createQuery(
            "SELECT DISTINCT f FROM FichePaie f LEFT JOIN FETCH f.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE f.employe.id = :employeId ORDER BY f.annee DESC, f.mois DESC", 
            FichePaie.class
        );
        query.setParameter("employeId", employeId);
        return query.getResultList();
    }

    public List<FichePaie> findByPeriode(Integer mois, Integer annee) {
        TypedQuery<FichePaie> query = entityManager.createQuery(
            "SELECT DISTINCT f FROM FichePaie f LEFT JOIN FETCH f.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE f.mois = :mois AND f.annee = :annee ORDER BY e.nom, e.prenom", 
            FichePaie.class
        );
        query.setParameter("mois", mois);
        query.setParameter("annee", annee);
        return query.getResultList();
    }

    public List<FichePaie> findByStatut(StatutFichePaie statut) {
        TypedQuery<FichePaie> query = entityManager.createQuery(
            "SELECT DISTINCT f FROM FichePaie f LEFT JOIN FETCH f.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE f.statut = :statut ORDER BY f.annee DESC, f.mois DESC", 
            FichePaie.class
        );
        query.setParameter("statut", statut);
        return query.getResultList();
    }

    public Optional<FichePaie> findByEmployeAndPeriode(Long employeId, Integer mois, Integer annee) {
        try {
            FichePaie fiche = entityManager.createQuery(
                "SELECT f FROM FichePaie f WHERE f.employe.id = :employeId AND f.mois = :mois AND f.annee = :annee", 
                FichePaie.class
            )
            .setParameter("employeId", employeId)
            .setParameter("mois", mois)
            .setParameter("annee", annee)
            .getSingleResult();
            return Optional.of(fiche);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Compte toutes les fiches de paie
     */
    public long countAll() {
        return entityManager.createQuery("SELECT COUNT(f) FROM FichePaie f", Long.class).getSingleResult();
    }

    /**
     * Recherche une fiche de paie par ID avec toutes les relations chargées
     */
    public Optional<FichePaie> findByIdWithRelations(Long id) {
        try {
            TypedQuery<FichePaie> query = entityManager.createQuery(
                "SELECT DISTINCT f FROM FichePaie f LEFT JOIN FETCH f.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE f.id = :id", 
                FichePaie.class
            );
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retourne toutes les fiches de paie avec relations chargées
     */
    @Override
    public List<FichePaie> findAll() {
        TypedQuery<FichePaie> query = entityManager.createQuery(
            "SELECT DISTINCT f FROM FichePaie f " +
            "LEFT JOIN FETCH f.employe e " +
            "LEFT JOIN FETCH e.departement " +
            "LEFT JOIN FETCH e.poste " +
            "ORDER BY f.annee DESC, f.mois DESC", 
            FichePaie.class
        );
        return query.getResultList();
    }

    /**
     * Compte les fiches de paie par statut
     */
    public long countByStatut(StatutFichePaie statut) {
        return entityManager.createQuery(
            "SELECT COUNT(f) FROM FichePaie f WHERE f.statut = :statut", Long.class
        )
        .setParameter("statut", statut)
        .getSingleResult();
    }

    /**
     * Somme les montants par département pour une période donnée
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> sumByDepartement(Integer mois, Integer annee) {
        return entityManager.createQuery(
            "SELECT f.employe.departement.nom, " +
            "SUM(f.totalBrut) as totalBrut, " +
            "SUM(f.totalDeductions) as totalDeductions, " +
            "SUM(f.netAPayer) as totalNet, " +
            "COUNT(f) as nbFiches " +
            "FROM FichePaie f " +
            "WHERE f.mois = :mois AND f.annee = :annee AND f.employe.departement IS NOT NULL " +
            "GROUP BY f.employe.departement.nom " +
            "ORDER BY totalNet DESC"
        )
        .setParameter("mois", mois)
        .setParameter("annee", annee)
        .getResultList();
    }
}

