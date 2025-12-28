package ma.projet.rh.repositories;

import ma.projet.rh.entities.Conge;
import ma.projet.rh.enums.StatutConge;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Conge
 */
@Stateless
public class CongeRepository extends GenericRepository<Conge, Long> {

    public CongeRepository() {
        super(Conge.class);
    }

    /**
     * Retourne les congés d'un employé (avec relations chargées)
     */
    public List<Conge> findByEmploye(Long employeId) {
        TypedQuery<Conge> query = entityManager.createQuery(
            "SELECT DISTINCT c FROM Conge c LEFT JOIN FETCH c.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE c.employe.id = :employeId ORDER BY c.dateDemande DESC", 
            Conge.class
        );
        query.setParameter("employeId", employeId);
        return query.getResultList();
    }

    /**
     * Retourne les congés par statut (avec relations chargées)
     */
    public List<Conge> findByStatut(StatutConge statut) {
        TypedQuery<Conge> query = entityManager.createQuery(
            "SELECT DISTINCT c FROM Conge c LEFT JOIN FETCH c.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE c.statut = :statut ORDER BY c.dateDemande", 
            Conge.class
        );
        query.setParameter("statut", statut);
        return query.getResultList();
    }

    /**
     * Retourne les congés en attente de validation par un manager (avec relations chargées)
     */
    public List<Conge> findEnAttenteManager(Long managerId) {
        TypedQuery<Conge> query = entityManager.createQuery(
            "SELECT DISTINCT c FROM Conge c LEFT JOIN FETCH c.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE c.statut = ma.projet.rh.enums.StatutConge.EN_ATTENTE AND e.manager.id = :managerId ORDER BY c.dateDemande", 
            Conge.class
        );
        query.setParameter("managerId", managerId);
        return query.getResultList();
    }

    /**
     * Retourne les congés en attente de validation RH (avec relations chargées)
     */
    public List<Conge> findEnAttenteRH() {
        TypedQuery<Conge> query = entityManager.createQuery(
            "SELECT DISTINCT c FROM Conge c LEFT JOIN FETCH c.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste WHERE c.statut = ma.projet.rh.enums.StatutConge.VALIDE_MANAGER ORDER BY c.dateDemande", 
            Conge.class
        );
        return query.getResultList();
    }

    /**
     * Retourne un congé par ID avec toutes les relations chargées
     */
    public Optional<Conge> findByIdWithRelations(Long id) {
        TypedQuery<Conge> query = entityManager.createQuery(
            "SELECT DISTINCT c FROM Conge c LEFT JOIN FETCH c.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste LEFT JOIN FETCH e.manager WHERE c.id = :id", 
            Conge.class
        );
        query.setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retourne les congés approuvés pour une période donnée
     */
    public List<Conge> findByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        TypedQuery<Conge> query = entityManager.createNamedQuery("Conge.findByPeriode", Conge.class);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    /**
     * Compte les congés en attente pour un manager
     */
    public long countEnAttenteManager(Long managerId) {
        return entityManager.createQuery(
            "SELECT COUNT(c) FROM Conge c WHERE c.statut = ma.projet.rh.enums.StatutConge.EN_ATTENTE AND c.employe.manager.id = :managerId", 
            Long.class
        )
        .setParameter("managerId", managerId)
        .getSingleResult();
    }

    /**
     * Compte les congés en attente de validation RH
     */
    public long countEnAttenteRH() {
        return entityManager.createQuery(
            "SELECT COUNT(c) FROM Conge c WHERE c.statut = ma.projet.rh.enums.StatutConge.VALIDE_MANAGER", 
            Long.class
        )
        .getSingleResult();
    }

    /**
     * Vérifie s'il y a un chevauchement de congés pour un employé
     */
    public boolean hasOverlap(Long employeId, LocalDate dateDebut, LocalDate dateFin, Long excludeCongeId) {
        StringBuilder jpql = new StringBuilder(
            "SELECT COUNT(c) FROM Conge c WHERE c.employe.id = :employeId " +
            "AND c.statut IN (ma.projet.rh.enums.StatutConge.VALIDE_MANAGER, ma.projet.rh.enums.StatutConge.APPROUVE) " +
            "AND ((c.dateDebut <= :dateFin AND c.dateFin >= :dateDebut))"
        );
        
        if (excludeCongeId != null) {
            jpql.append(" AND c.id != :excludeId");
        }
        
        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        query.setParameter("employeId", employeId);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        
        if (excludeCongeId != null) {
            query.setParameter("excludeId", excludeCongeId);
        }
        
        return query.getSingleResult() > 0;
    }

    /**
     * Retourne tous les congés (avec relations chargées)
     */
    public List<Conge> findAllWithRelations() {
        return entityManager.createQuery(
            "SELECT DISTINCT c FROM Conge c LEFT JOIN FETCH c.employe e LEFT JOIN FETCH e.departement LEFT JOIN FETCH e.poste ORDER BY c.dateDemande DESC", 
            Conge.class
        ).getResultList();
    }

    /**
     * Compte tous les congés
     */
    public long countAll() {
        return entityManager.createQuery("SELECT COUNT(c) FROM Conge c", Long.class).getSingleResult();
    }

    /**
     * Compte les congés par statut
     */
    public long countByStatut(StatutConge statut) {
        return entityManager.createQuery(
            "SELECT COUNT(c) FROM Conge c WHERE c.statut = :statut", Long.class
        )
        .setParameter("statut", statut)
        .getSingleResult();
    }

    /**
     * Compte les congés par type
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> countByType() {
        return entityManager.createQuery(
            "SELECT c.typeConge, COUNT(c) FROM Conge c GROUP BY c.typeConge ORDER BY COUNT(c) DESC"
        ).getResultList();
    }

    /**
     * Compte les congés par mois pour une année donnée (utilise des fonctions natives MySQL)
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> countByMois(int annee) {
        // Utiliser une requête native car MONTH() et YEAR() ne sont pas standardes en JPQL
        return entityManager.createNativeQuery(
            "SELECT MONTH(date_debut) as mois, COUNT(*) as total FROM conges WHERE YEAR(date_debut) = :annee GROUP BY MONTH(date_debut) ORDER BY MONTH(date_debut)"
        )
        .setParameter("annee", annee)
        .getResultList();
    }

    /**
     * Retourne les top employés avec le plus de congés
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findTopEmployesWithConges(int limit) {
        return entityManager.createQuery(
            "SELECT c.employe.nom, c.employe.prenom, COUNT(c) as nbConges FROM Conge c GROUP BY c.employe.id, c.employe.nom, c.employe.prenom ORDER BY nbConges DESC"
        )
        .setMaxResults(limit)
        .getResultList();
    }
}

