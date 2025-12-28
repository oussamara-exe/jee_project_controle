package ma.projet.rh.repositories;

import ma.projet.rh.entities.Employe;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Employe
 */
@Stateless
public class EmployeRepository extends GenericRepository<Employe, Long> {

    public EmployeRepository() {
        super(Employe.class);
    }

    /**
     * Recherche un employé par son matricule
     */
    public Optional<Employe> findByMatricule(String matricule) {
        TypedQuery<Employe> query = entityManager.createNamedQuery("Employe.findByMatricule", Employe.class);
        query.setParameter("matricule", matricule);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Recherche un employé par son email
     */
    public Optional<Employe> findByEmail(String email) {
        TypedQuery<Employe> query = entityManager.createNamedQuery("Employe.findByEmail", Employe.class);
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    /**
     * Retourne les employés d'un département
     */
    public List<Employe> findByDepartement(Long departementId) {
        TypedQuery<Employe> query = entityManager.createNamedQuery("Employe.findByDepartement", Employe.class);
        query.setParameter("departementId", departementId);
        return query.getResultList();
    }

    /**
     * Retourne les subordonnés d'un manager
     */
    public List<Employe> findByManager(Long managerId) {
        TypedQuery<Employe> query = entityManager.createNamedQuery("Employe.findByManager", Employe.class);
        query.setParameter("managerId", managerId);
        return query.getResultList();
    }

    /**
     * Compte le nombre d'employés actifs
     */
    public long countActifs() {
        return entityManager.createQuery(
            "SELECT COUNT(e) FROM Employe e WHERE e.actif = true", Long.class
        ).getSingleResult();
    }

    /**
     * Compte le nombre d'employés inactifs
     */
    public long countInactifs() {
        return entityManager.createQuery(
            "SELECT COUNT(e) FROM Employe e WHERE e.actif = false", Long.class
        ).getSingleResult();
    }

    /**
     * Compte le nombre de départements
     */
    public long countDepartements() {
        return entityManager.createQuery(
            "SELECT COUNT(DISTINCT e.departement) FROM Employe e WHERE e.departement IS NOT NULL", Long.class
        ).getSingleResult();
    }

    /**
     * Compte le nombre de postes
     */
    public long countPostes() {
        return entityManager.createQuery(
            "SELECT COUNT(DISTINCT e.poste) FROM Employe e WHERE e.poste IS NOT NULL", Long.class
        ).getSingleResult();
    }

    /**
     * Compte les employés par département
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> countByDepartement() {
        return entityManager.createQuery(
            "SELECT e.departement.nom, COUNT(e) FROM Employe e WHERE e.actif = true AND e.departement IS NOT NULL GROUP BY e.departement.nom ORDER BY COUNT(e) DESC"
        ).getResultList();
    }

    /**
     * Compte les employés par poste
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> countByPoste() {
        return entityManager.createQuery(
            "SELECT e.poste.titre, COUNT(e) FROM Employe e WHERE e.actif = true AND e.poste IS NOT NULL GROUP BY e.poste.titre ORDER BY COUNT(e) DESC"
        ).getResultList();
    }

    /**
     * Recherche des employés par nom ou prénom (recherche partielle)
     */
    public List<Employe> search(String searchTerm) {
        String pattern = "%" + searchTerm.toLowerCase() + "%";
        return entityManager.createQuery(
            "SELECT e FROM Employe e WHERE e.actif = true AND " +
            "(LOWER(e.nom) LIKE :pattern OR LOWER(e.prenom) LIKE :pattern OR LOWER(e.matricule) LIKE :pattern OR LOWER(e.email) LIKE :pattern) " +
            "ORDER BY e.nom, e.prenom", 
            Employe.class
        )
        .setParameter("pattern", pattern)
        .getResultList();
    }

    /**
     * Recherche avancée avec critères multiples
     */
    public List<Employe> searchAdvanced(String nom, String prenom, Long departementId, Long posteId, Boolean actif) {
        StringBuilder jpql = new StringBuilder("SELECT e FROM Employe e WHERE 1=1");
        
        if (nom != null && !nom.isEmpty()) {
            jpql.append(" AND LOWER(e.nom) LIKE :nom");
        }
        if (prenom != null && !prenom.isEmpty()) {
            jpql.append(" AND LOWER(e.prenom) LIKE :prenom");
        }
        if (departementId != null) {
            jpql.append(" AND e.departement.id = :departementId");
        }
        if (posteId != null) {
            jpql.append(" AND e.poste.id = :posteId");
        }
        if (actif != null) {
            jpql.append(" AND e.actif = :actif");
        }
        
        jpql.append(" ORDER BY e.nom, e.prenom");
        
        TypedQuery<Employe> query = entityManager.createQuery(jpql.toString(), Employe.class);
        
        if (nom != null && !nom.isEmpty()) {
            query.setParameter("nom", "%" + nom.toLowerCase() + "%");
        }
        if (prenom != null && !prenom.isEmpty()) {
            query.setParameter("prenom", "%" + prenom.toLowerCase() + "%");
        }
        if (departementId != null) {
            query.setParameter("departementId", departementId);
        }
        if (posteId != null) {
            query.setParameter("posteId", posteId);
        }
        if (actif != null) {
            query.setParameter("actif", actif);
        }
        
        return query.getResultList();
    }

    /**
     * Retourne les managers (employés qui ont des subordonnés)
     */
    public List<Employe> findManagers() {
        return entityManager.createQuery(
            "SELECT DISTINCT e FROM Employe e WHERE e.actif = true AND SIZE(e.subordonnes) > 0 ORDER BY e.nom, e.prenom",
            Employe.class
        ).getResultList();
    }

    /**
     * Vérifie si un matricule existe déjà
     */
    public boolean existsByMatricule(String matricule) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(e) FROM Employe e WHERE e.matricule = :matricule", Long.class
        )
        .setParameter("matricule", matricule)
        .getSingleResult();
        return count > 0;
    }

    /**
     * Vérifie si un email existe déjà
     */
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(e) FROM Employe e WHERE e.email = :email", Long.class
        )
        .setParameter("email", email)
        .getSingleResult();
        return count > 0;
    }

    /**
     * Archive un employé (le marque comme inactif)
     */
    public void archive(Long employeId) {
        Employe employe = findById(employeId).orElse(null);
        if (employe != null) {
            employe.setActif(false);
            update(employe);
        }
    }

    /**
     * Restaure un employé archivé
     */
    public void restore(Long employeId) {
        Employe employe = findById(employeId).orElse(null);
        if (employe != null) {
            employe.setActif(true);
            update(employe);
        }
    }

    /**
     * Recherche un employé par ID avec toutes les relations chargées
     */
    public Optional<Employe> findByIdWithRelations(Long id) {
        try {
            TypedQuery<Employe> query = entityManager.createQuery(
                "SELECT DISTINCT e FROM Employe e " +
                "LEFT JOIN FETCH e.departement " +
                "LEFT JOIN FETCH e.poste " +
                "LEFT JOIN FETCH e.manager " +
                "LEFT JOIN FETCH e.userAccount " +
                "WHERE e.id = :id", 
                Employe.class
            );
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retourne tous les employés avec leurs relations chargées
     */
    @Override
    public List<Employe> findAll() {
        TypedQuery<Employe> query = entityManager.createQuery(
            "SELECT DISTINCT e FROM Employe e " +
            "LEFT JOIN FETCH e.departement " +
            "LEFT JOIN FETCH e.poste " +
            "ORDER BY e.nom, e.prenom", 
            Employe.class
        );
        return query.getResultList();
    }

    /**
     * Retourne tous les employés actifs avec leurs relations chargées
     */
    public List<Employe> findActifs() {
        TypedQuery<Employe> query = entityManager.createQuery(
            "SELECT DISTINCT e FROM Employe e " +
            "LEFT JOIN FETCH e.departement " +
            "LEFT JOIN FETCH e.poste " +
            "WHERE e.actif = true " +
            "ORDER BY e.nom, e.prenom", 
            Employe.class
        );
        return query.getResultList();
    }

    /**
     * Retourne tous les employés actifs avec pagination et relations
     */
    public List<Employe> findActifs(int page, int pageSize) {
        TypedQuery<Employe> query = entityManager.createQuery(
            "SELECT DISTINCT e FROM Employe e " +
            "LEFT JOIN FETCH e.departement " +
            "LEFT JOIN FETCH e.poste " +
            "WHERE e.actif = true " +
            "ORDER BY e.nom, e.prenom", 
            Employe.class
        );
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
}
