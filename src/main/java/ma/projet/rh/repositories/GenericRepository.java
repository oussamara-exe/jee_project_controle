package ma.projet.rh.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Repository générique de base avec les opérations CRUD standard
 * Tous les repositories spécifiques héritent de cette classe
 *
 * @param <T> Type de l'entité
 * @param <ID> Type de l'identifiant
 */
public abstract class GenericRepository<T, ID extends Serializable> {

    @PersistenceContext(unitName = "gestion-rh-pu")
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    protected GenericRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Persiste une nouvelle entité
     */
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * Met à jour une entité existante
     */
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    /**
     * Recherche une entité par son ID
     */
    public Optional<T> findById(ID id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    /**
     * Supprime une entité
     */
    public void delete(T entity) {
        if (entityManager.contains(entity)) {
            entityManager.remove(entity);
        } else {
            entityManager.remove(entityManager.merge(entity));
        }
    }

    /**
     * Supprime une entité par son ID
     */
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Retourne toutes les entités
     */
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);
        return entityManager.createQuery(cq).getResultList();
    }

    /**
     * Compte le nombre total d'entités
     */
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));
        return entityManager.createQuery(cq).getSingleResult();
    }

    /**
     * Vérifie si une entité existe par son ID
     */
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    /**
     * Retourne toutes les entités avec pagination
     */
    public List<T> findAll(int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);
        
        TypedQuery<T> query = entityManager.createQuery(cq);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        
        return query.getResultList();
    }

    /**
     * Flush les modifications vers la base de données
     */
    public void flush() {
        entityManager.flush();
    }

    /**
     * Rafraîchit une entité depuis la base de données
     */
    public void refresh(T entity) {
        entityManager.refresh(entity);
    }

    /**
     * Détache une entité du contexte de persistance
     */
    public void detach(T entity) {
        entityManager.detach(entity);
    }

    /**
     * Exécute une named query
     */
    protected List<T> executeNamedQuery(String queryName) {
        return entityManager.createNamedQuery(queryName, entityClass).getResultList();
    }

    /**
     * Exécute une named query avec un seul résultat
     */
    protected Optional<T> executeSingleResultNamedQuery(String queryName) {
        try {
            T result = entityManager.createNamedQuery(queryName, entityClass).getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retourne le gestionnaire d'entités (pour les requêtes complexes)
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Retourne la classe de l'entité
     */
    protected Class<T> getEntityClass() {
        return entityClass;
    }
}

