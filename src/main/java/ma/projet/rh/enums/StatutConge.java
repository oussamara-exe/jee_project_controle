package ma.projet.rh.enums;

/**
 * Énumération des statuts d'une demande de congé
 * Représente le workflow de validation
 */
public enum StatutConge {
    EN_ATTENTE("En attente", "La demande est soumise et attend validation"),
    VALIDE_MANAGER("Validé par manager", "Le manager a approuvé la demande"),
    VALIDE_RH("Validé par RH", "Les RH ont approuvé la demande"),
    APPROUVE("Approuvé", "La demande est complètement approuvée"),
    REFUSE("Refusé", "La demande a été refusée");

    private final String libelle;
    private final String description;

    StatutConge(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Vérifie si le statut permet une modification
     */
    public boolean isModifiable() {
        return this == EN_ATTENTE || this == REFUSE;
    }

    /**
     * Retourne le statut suivant dans le workflow
     */
    public StatutConge getNextStatut() {
        switch (this) {
            case EN_ATTENTE:
                return VALIDE_MANAGER;
            case VALIDE_MANAGER:
                return VALIDE_RH;
            case VALIDE_RH:
                return APPROUVE;
            default:
                return this;
        }
    }
}

