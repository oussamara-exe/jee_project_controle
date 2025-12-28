package ma.projet.rh.repositories;

import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.Role;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité UserAccount
 */
@Stateless
public class UserAccountRepository extends GenericRepository<UserAccount, Long> {

    public UserAccountRepository() {
        super(UserAccount.class);
    }

    /**
     * Recherche un compte par username
     */
    public Optional<UserAccount> findByUsername(String username) {
        // Charger la relation employe avec JOIN FETCH pour éviter LazyInitializationException
        TypedQuery<UserAccount> query = entityManager.createQuery(
            "SELECT DISTINCT u FROM UserAccount u LEFT JOIN FETCH u.employe WHERE u.username = :username", 
            UserAccount.class
        );
        query.setParameter("username", username);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retourne tous les comptes actifs
     */
    public List<UserAccount> findActive() {
        return entityManager.createNamedQuery("UserAccount.findActive", UserAccount.class).getResultList();
    }

    /**
     * Retourne les comptes par rôle
     */
    public List<UserAccount> findByRole(Role role) {
        return entityManager.createQuery(
            "SELECT u FROM UserAccount u WHERE u.role = :role ORDER BY u.username", UserAccount.class
        )
        .setParameter("role", role)
        .getResultList();
    }

    /**
     * Vérifie si un username existe déjà
     */
    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(u) FROM UserAccount u WHERE u.username = :username", Long.class
        )
        .setParameter("username", username)
        .getSingleResult();
        return count > 0;
    }

    /**
     * Retourne tous les utilisateurs avec leurs employés chargés
     */
    @Override
    public List<UserAccount> findAll() {
        TypedQuery<UserAccount> query = entityManager.createQuery(
            "SELECT DISTINCT u FROM UserAccount u " +
            "LEFT JOIN FETCH u.employe " +
            "ORDER BY u.username", 
            UserAccount.class
        );
        return query.getResultList();
    }
}

