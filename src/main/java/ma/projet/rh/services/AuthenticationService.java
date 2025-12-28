package ma.projet.rh.services;

import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.TypeAction;
import ma.projet.rh.repositories.UserAccountRepository;
import ma.projet.rh.utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Service d'authentification et de gestion de session
 */
@Stateless
@Transactional
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    @Inject
    private UserAccountRepository userAccountRepository;

    @Inject
    private HistoriqueService historiqueService;

    /**
     * Authentifie un utilisateur
     *
     * @return UserAccount si authentification réussie, Optional.empty() sinon
     */
    public Optional<UserAccount> authenticate(String username, String password, String ipAddress) {
        logger.info("Tentative d'authentification pour l'utilisateur: {}", username);

        Optional<UserAccount> userOpt = userAccountRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            logger.warn("Utilisateur non trouvé: {}", username);
            return Optional.empty();
        }

        UserAccount user = userOpt.get();

        // Vérifier si le compte est actif
        if (!user.getActif()) {
            logger.warn("Compte désactivé: {}", username);
            return Optional.empty();
        }

        // Vérifier si le compte est verrouillé
        if (user.isVerrouille()) {
            logger.warn("Compte verrouillé: {}", username);
            return Optional.empty();
        }

        // Vérifier le mot de passe
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            logger.warn("Mot de passe incorrect pour l'utilisateur: {}", username);
            
            // Incrémenter les tentatives échouées
            user.incrementerTentativesEchouees();
            userAccountRepository.update(user);
            
            // Enregistrer dans l'historique
            historiqueService.enregistrer(
                user,
                TypeAction.CONNEXION,
                "UserAccount",
                user.getId(),
                "Tentative de connexion échouée",
                ipAddress
            );
            
            return Optional.empty();
        }

        // Authentification réussie
        user.enregistrerConnexion();
        userAccountRepository.update(user);

        // Enregistrer dans l'historique
        historiqueService.enregistrer(
            user,
            TypeAction.CONNEXION,
            "UserAccount",
            user.getId(),
            "Connexion réussie",
            ipAddress
        );

        logger.info("Authentification réussie pour l'utilisateur: {}", username);
        return Optional.of(user);
    }

    /**
     * Déconnexion d'un utilisateur
     */
    public void logout(UserAccount user, String ipAddress) {
        logger.info("Déconnexion de l'utilisateur: {}", user.getUsername());

        // Enregistrer dans l'historique
        historiqueService.enregistrer(
            user,
            TypeAction.DECONNEXION,
            "UserAccount",
            user.getId(),
            "Déconnexion",
            ipAddress
        );
    }

    /**
     * Change le mot de passe d'un utilisateur
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        logger.info("Changement de mot de passe pour l'utilisateur: {}", userId);

        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            return false;
        }

        UserAccount user = userOpt.get();

        // Vérifier l'ancien mot de passe
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            logger.warn("Ancien mot de passe incorrect");
            return false;
        }

        // Valider le nouveau mot de passe
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne respecte pas les critères de sécurité");
        }

        // Changer le mot de passe
        user.setPassword(PasswordUtil.hashPassword(newPassword));
        userAccountRepository.update(user);

        // Enregistrer dans l'historique
        historiqueService.enregistrer(
            user,
            TypeAction.MODIFICATION,
            "UserAccount",
            user.getId(),
            "Changement de mot de passe",
            null
        );

        logger.info("Mot de passe changé avec succès pour l'utilisateur: {}", userId);
        return true;
    }

    /**
     * Réinitialise le mot de passe d'un utilisateur
     */
    public String resetPassword(Long userId) {
        logger.info("Réinitialisation du mot de passe pour l'utilisateur: {}", userId);

        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        UserAccount user = userOpt.get();

        // Générer un nouveau mot de passe temporaire
        String tempPassword = PasswordUtil.generateTemporaryPassword();
        user.setPassword(PasswordUtil.hashPassword(tempPassword));
        user.resetTentativesEchouees(); // Déverrouiller le compte si nécessaire
        
        userAccountRepository.update(user);

        logger.info("Mot de passe réinitialisé pour l'utilisateur: {}", userId);
        
        return tempPassword; // À envoyer par email à l'utilisateur
    }

    /**
     * Déverrouille un compte utilisateur
     */
    public void unlockAccount(Long userId) {
        logger.info("Déverrouillage du compte: {}", userId);

        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            user.resetTentativesEchouees();
            userAccountRepository.update(user);
            
            logger.info("Compte déverrouillé: {}", userId);
        }
    }

    /**
     * Active un compte utilisateur
     */
    public void activateAccount(Long userId) {
        logger.info("Activation du compte: {}", userId);

        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            user.setActif(true);
            userAccountRepository.update(user);
            
            logger.info("Compte activé: {}", userId);
        }
    }

    /**
     * Désactive un compte utilisateur
     */
    public void deactivateAccount(Long userId) {
        logger.info("Désactivation du compte: {}", userId);

        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            user.setActif(false);
            userAccountRepository.update(user);
            
            logger.info("Compte désactivé: {}", userId);
        }
    }
}

