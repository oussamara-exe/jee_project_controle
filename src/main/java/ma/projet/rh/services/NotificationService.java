package ma.projet.rh.services;

import ma.projet.rh.entities.Notification;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.TypeNotification;
import ma.projet.rh.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des notifications
 */
@Stateless
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Inject
    private NotificationRepository notificationRepository;

    /**
     * Crée une nouvelle notification
     */
    public Notification creerNotification(UserAccount destinataire, TypeNotification type, String message) {
        return creerNotification(destinataire, type, message, null);
    }

    /**
     * Crée une nouvelle notification avec un lien
     */
    public Notification creerNotification(UserAccount destinataire, TypeNotification type, String message, String lien) {
        logger.debug("Création d'une notification pour l'utilisateur: {}", destinataire.getUsername());

        Notification notification = new Notification(destinataire, type, message, lien);
        Notification savedNotification = notificationRepository.save(notification);

        logger.info("Notification créée: {}", savedNotification.getId());
        return savedNotification;
    }

    /**
     * Retourne les notifications d'un utilisateur
     */
    public List<Notification> findByDestinataire(Long destinataireId) {
        return notificationRepository.findByDestinataire(destinataireId);
    }

    /**
     * Retourne les notifications non lues d'un utilisateur
     */
    public List<Notification> findNonLues(Long destinataireId) {
        return notificationRepository.findNonLues(destinataireId);
    }

    /**
     * Compte les notifications non lues
     */
    public long countNonLues(Long destinataireId) {
        return notificationRepository.countNonLues(destinataireId);
    }

    /**
     * Marque une notification comme lue
     */
    public void marquerCommeLue(Long notificationId) {
        Optional<Notification> notifOpt = notificationRepository.findById(notificationId);
        
        if (notifOpt.isPresent()) {
            Notification notification = notifOpt.get();
            notification.marquerCommeLue();
            notificationRepository.update(notification);
            logger.debug("Notification marquée comme lue: {}", notificationId);
        }
    }

    /**
     * Marque toutes les notifications comme lues
     */
    public void marquerToutesCommeLues(Long destinataireId) {
        notificationRepository.marquerToutesCommeLues(destinataireId);
        logger.info("Toutes les notifications marquées comme lues pour l'utilisateur: {}", destinataireId);
    }

    /**
     * Supprime une notification
     */
    public void supprimer(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        logger.info("Notification supprimée: {}", notificationId);
    }

    /**
     * Supprime toutes les notifications d'un utilisateur
     */
    public void supprimerToutes(Long destinataireId) {
        List<Notification> notifications = findByDestinataire(destinataireId);
        for (Notification notification : notifications) {
            notificationRepository.delete(notification);
        }
        logger.info("Toutes les notifications supprimées pour l'utilisateur: {}", destinataireId);
    }
}

