package ma.projet.rh.services;

import ma.projet.rh.entities.Employe;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.Role;
import ma.projet.rh.repositories.EmployeRepository;
import ma.projet.rh.repositories.UserAccountRepository;
import ma.projet.rh.utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des employés
 */
@Stateless
@Transactional
public class EmployeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeService.class);

    @Inject
    private EmployeRepository employeRepository;

    @Inject
    private UserAccountRepository userAccountRepository;

    /**
     * Crée un nouvel employé avec son compte utilisateur
     */
    public Employe create(Employe employe, String username, String password, Role role) {
        logger.info("Création d'un nouvel employé: {}", employe.getMatricule());

        // Vérifier que le matricule n'existe pas déjà
        if (employeRepository.existsByMatricule(employe.getMatricule())) {
            throw new IllegalArgumentException("Le matricule " + employe.getMatricule() + " existe déjà");
        }

        // Vérifier que l'email n'existe pas déjà
        if (employeRepository.existsByEmail(employe.getEmail())) {
            throw new IllegalArgumentException("L'email " + employe.getEmail() + " existe déjà");
        }

        // Créer le compte utilisateur
        if (username != null && password != null) {
            if (userAccountRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Le nom d'utilisateur " + username + " existe déjà");
            }

            UserAccount userAccount = new UserAccount();
            userAccount.setUsername(username);
            userAccount.setPassword(PasswordUtil.hashPassword(password));
            userAccount.setRole(role != null ? role : Role.EMPLOYE);
            userAccount.setActif(true);

            userAccount = userAccountRepository.save(userAccount);
            employe.setUserAccount(userAccount);
        }

        // Sauvegarder l'employé
        Employe savedEmploye = employeRepository.save(employe);
        logger.info("Employé créé avec succès: {}", savedEmploye.getId());

        return savedEmploye;
    }

    /**
     * Met à jour un employé existant
     */
    public Employe update(Employe employe) {
        logger.info("Mise à jour de l'employé: {}", employe.getId());

        // Vérifier que l'employé existe
        if (!employeRepository.existsById(employe.getId())) {
            throw new IllegalArgumentException("L'employé n'existe pas");
        }

        // Vérifier l'unicité du matricule
        Optional<Employe> existingByMatricule = employeRepository.findByMatricule(employe.getMatricule());
        if (existingByMatricule.isPresent() && !existingByMatricule.get().getId().equals(employe.getId())) {
            throw new IllegalArgumentException("Le matricule existe déjà pour un autre employé");
        }

        // Vérifier l'unicité de l'email
        Optional<Employe> existingByEmail = employeRepository.findByEmail(employe.getEmail());
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(employe.getId())) {
            throw new IllegalArgumentException("L'email existe déjà pour un autre employé");
        }

        Employe updatedEmploye = employeRepository.update(employe);
        logger.info("Employé mis à jour avec succès: {}", updatedEmploye.getId());

        return updatedEmploye;
    }

    /**
     * Recherche un employé par son ID
     */
    public Optional<Employe> findById(Long id) {
        return employeRepository.findById(id);
    }

    public Optional<Employe> findByIdWithRelations(Long id) {
        return employeRepository.findByIdWithRelations(id);
    }

    /**
     * Recherche un employé par son matricule
     */
    public Optional<Employe> findByMatricule(String matricule) {
        return employeRepository.findByMatricule(matricule);
    }

    /**
     * Recherche un employé par son email
     */
    public Optional<Employe> findByEmail(String email) {
        return employeRepository.findByEmail(email);
    }

    /**
     * Retourne tous les employés
     */
    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    /**
     * Retourne tous les employés actifs
     */
    public List<Employe> findActifs() {
        return employeRepository.findActifs();
    }

    /**
     * Retourne les employés actifs avec pagination
     */
    public List<Employe> findActifs(int page, int pageSize) {
        return employeRepository.findActifs(page, pageSize);
    }

    /**
     * Retourne les employés d'un département
     */
    public List<Employe> findByDepartement(Long departementId) {
        return employeRepository.findByDepartement(departementId);
    }

    /**
     * Retourne les subordonnés d'un manager
     */
    public List<Employe> findByManager(Long managerId) {
        return employeRepository.findByManager(managerId);
    }

    /**
     * Recherche des employés (nom, prénom, matricule, email)
     */
    public List<Employe> search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findActifs();
        }
        return employeRepository.search(searchTerm.trim());
    }

    /**
     * Recherche avancée avec critères multiples
     */
    public List<Employe> searchAdvanced(String nom, String prenom, Long departementId, Long posteId, Boolean actif) {
        return employeRepository.searchAdvanced(nom, prenom, departementId, posteId, actif);
    }

    /**
     * Retourne les managers
     */
    public List<Employe> findManagers() {
        return employeRepository.findManagers();
    }

    /**
     * Compte les employés actifs
     */
    public long countActifs() {
        return employeRepository.countActifs();
    }

    /**
     * Compte tous les employés
     */
    public long count() {
        return employeRepository.count();
    }

    /**
     * Archive un employé (le marque comme inactif)
     */
    public void archive(Long employeId) {
        logger.info("Archivage de l'employé: {}", employeId);
        employeRepository.archive(employeId);
        
        // Désactiver également le compte utilisateur
        Optional<Employe> employe = findById(employeId);
        if (employe.isPresent() && employe.get().getUserAccount() != null) {
            UserAccount userAccount = employe.get().getUserAccount();
            userAccount.setActif(false);
            userAccountRepository.update(userAccount);
        }
        
        logger.info("Employé archivé avec succès: {}", employeId);
    }

    /**
     * Restaure un employé archivé
     */
    public void restore(Long employeId) {
        logger.info("Restauration de l'employé: {}", employeId);
        employeRepository.restore(employeId);
        
        // Réactiver également le compte utilisateur
        Optional<Employe> employe = findById(employeId);
        if (employe.isPresent() && employe.get().getUserAccount() != null) {
            UserAccount userAccount = employe.get().getUserAccount();
            userAccount.setActif(true);
            userAccountRepository.update(userAccount);
        }
        
        logger.info("Employé restauré avec succès: {}", employeId);
    }

    /**
     * Supprime un employé (suppression définitive)
     */
    public void delete(Long employeId) {
        logger.warn("Suppression définitive de l'employé: {}", employeId);
        
        Optional<Employe> employe = findById(employeId);
        if (employe.isPresent()) {
            // Supprimer d'abord le compte utilisateur si existe
            if (employe.get().getUserAccount() != null) {
                userAccountRepository.delete(employe.get().getUserAccount());
            }
            
            // Puis supprimer l'employé
            employeRepository.delete(employe.get());
            logger.info("Employé supprimé définitivement: {}", employeId);
        }
    }

    /**
     * Vérifie si un matricule existe
     */
    public boolean existsByMatricule(String matricule) {
        return employeRepository.existsByMatricule(matricule);
    }

    /**
     * Vérifie si un email existe
     */
    public boolean existsByEmail(String email) {
        return employeRepository.existsByEmail(email);
    }
}

