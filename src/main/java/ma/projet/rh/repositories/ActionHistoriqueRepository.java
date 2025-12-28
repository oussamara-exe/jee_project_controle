package ma.projet.rh.repositories;

import ma.projet.rh.entities.ActionHistorique;
import ma.projet.rh.enums.TypeAction;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité ActionHistorique
 */
@Stateless
public class ActionHistoriqueRepository extends GenericRepository<ActionHistorique, Long> {

    public ActionHistoriqueRepository() {
        super(ActionHistorique.class);
    }

    /**
     * Retourne les actions d'un utilisateur
     */
    public List<ActionHistorique> findByUtilisateur(Long utilisateurId) {
        TypedQuery<ActionHistorique> query = entityManager.createNamedQuery("ActionHistorique.findByUtilisateur", ActionHistorique.class);
        query.setParameter("utilisateurId", utilisateurId);
        return query.getResultList();
    }

    /**
     * Retourne les actions par type
     */
    public List<ActionHistorique> findByType(TypeAction typeAction) {
        TypedQuery<ActionHistorique> query = entityManager.createNamedQuery("ActionHistorique.findByType", ActionHistorique.class);
        query.setParameter("typeAction", typeAction);
        return query.getResultList();
    }

    /**
     * Retourne les actions pour une entité spécifique
     */
    public List<ActionHistorique> findByEntite(String entiteType, Long entiteId) {
        TypedQuery<ActionHistorique> query = entityManager.createNamedQuery("ActionHistorique.findByEntite", ActionHistorique.class);
        query.setParameter("entiteType", entiteType);
        query.setParameter("entiteId", entiteId);
        return query.getResultList();
    }

    /**
     * Retourne les actions pour une période
     */
    public List<ActionHistorique> findByPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        TypedQuery<ActionHistorique> query = entityManager.createNamedQuery("ActionHistorique.findByPeriode", ActionHistorique.class);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    /**
     * Retourne toutes les actions historiques avec utilisateur chargé
     */
    @Override
    public List<ActionHistorique> findAll() {
        TypedQuery<ActionHistorique> query = entityManager.createQuery(
            "SELECT DISTINCT a FROM ActionHistorique a " +
            "LEFT JOIN FETCH a.utilisateur " +
            "ORDER BY a.dateAction DESC", 
            ActionHistorique.class
        );
        return query.getResultList();
    }
}

