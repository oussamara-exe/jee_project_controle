package ma.projet.rh.services;

import ma.projet.rh.entities.ActionHistorique;
import ma.projet.rh.entities.UserAccount;
import ma.projet.rh.enums.TypeAction;
import ma.projet.rh.repositories.ActionHistoriqueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour la gestion de l'historique et de l'audit
 */
@Stateless
@Transactional
public class HistoriqueService {

    private static final Logger logger = LoggerFactory.getLogger(HistoriqueService.class);

    @Inject
    private ActionHistoriqueRepository historiqueRepository;

    /**
     * Enregistre une action dans l'historique
     */
    public void enregistrer(UserAccount utilisateur, TypeAction typeAction, String entiteType, Long entiteId, 
                           String description, String ipAddress) {
        ActionHistorique action = new ActionHistorique(utilisateur, typeAction, entiteType, entiteId, description);
        action.setAdresseIP(ipAddress);
        
        historiqueRepository.save(action);
        logger.debug("Action enregistrée dans l'historique: {} - {}", typeAction, description);
    }

    /**
     * Enregistre une action avec valeurs avant/après
     */
    public void enregistrerAvecModifications(UserAccount utilisateur, TypeAction typeAction, String entiteType, 
                                            Long entiteId, String description, String valeursAvant, 
                                            String valeursApres, String ipAddress) {
        ActionHistorique action = new ActionHistorique(utilisateur, typeAction, entiteType, entiteId, description);
        action.setValeursAvant(valeursAvant);
        action.setValeursApres(valeursApres);
        action.setAdresseIP(ipAddress);
        
        historiqueRepository.save(action);
        logger.debug("Action avec modifications enregistrée: {} - {}", typeAction, description);
    }

    /**
     * Retourne l'historique d'un utilisateur
     */
    public List<ActionHistorique> findByUtilisateur(Long utilisateurId) {
        return historiqueRepository.findByUtilisateur(utilisateurId);
    }

    /**
     * Retourne l'historique par type d'action
     */
    public List<ActionHistorique> findByType(TypeAction typeAction) {
        return historiqueRepository.findByType(typeAction);
    }

    /**
     * Retourne l'historique pour une entité
     */
    public List<ActionHistorique> findByEntite(String entiteType, Long entiteId) {
        return historiqueRepository.findByEntite(entiteType, entiteId);
    }

    /**
     * Retourne l'historique pour une période
     */
    public List<ActionHistorique> findByPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return historiqueRepository.findByPeriode(dateDebut, dateFin);
    }

    /**
     * Retourne tout l'historique
     */
    public List<ActionHistorique> findAll() {
        return historiqueRepository.findAll();
    }
}

