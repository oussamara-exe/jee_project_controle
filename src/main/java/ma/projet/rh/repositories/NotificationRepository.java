package ma.projet.rh.repositories;

import ma.projet.rh.entities.Notification;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Repository pour l'entité Notification
 */
@Stateless
public class NotificationRepository extends GenericRepository<Notification, Long> {

    public NotificationRepository() {
        super(Notification.class);
    }

    /**
     * Retourne les notifications d'un utilisateur (avec destinataire chargé pour éviter LazyInitializationException)
     */
    public List<Notification> findByDestinataire(Long destinataireId) {
        TypedQuery<Notification> query = entityManager.createQuery(
            "SELECT DISTINCT n FROM Notification n " +
            "LEFT JOIN FETCH n.destinataire " +
            "WHERE n.destinataire.id = :destinataireId " +
            "ORDER BY n.dateCreation DESC", 
            Notification.class
        );
        query.setParameter("destinataireId", destinataireId);
        return query.getResultList();
    }

    /**
     * Retourne les notifications non lues d'un utilisateur
     */
    public List<Notification> findNonLues(Long destinataireId) {
        TypedQuery<Notification> query = entityManager.createNamedQuery("Notification.findNonLues", Notification.class);
        query.setParameter("destinataireId", destinataireId);
        return query.getResultList();
    }

    /**
     * Compte les notifications non lues
     */
    public long countNonLues(Long destinataireId) {
        TypedQuery<Long> query = entityManager.createNamedQuery("Notification.countNonLues", Long.class);
        query.setParameter("destinataireId", destinataireId);
        return query.getSingleResult();
    }

    /**
     * Marque toutes les notifications comme lues
     */
    public void marquerToutesCommeLues(Long destinataireId) {
        entityManager.createQuery(
            "UPDATE Notification n SET n.lu = true, n.dateLecture = CURRENT_TIMESTAMP WHERE n.destinataire.id = :destinataireId AND n.lu = false"
        )
        .setParameter("destinataireId", destinataireId)
        .executeUpdate();
    }
}

