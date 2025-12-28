package ma.projet.rh.enums;

/**
 * Énumération des types d'actions enregistrées dans l'historique
 */
public enum TypeAction {
    CREATION("Création", "Création d'une entité"),
    MODIFICATION("Modification", "Modification d'une entité"),
    SUPPRESSION("Suppression", "Suppression d'une entité"),
    VALIDATION("Validation", "Validation d'une demande"),
    REFUS("Refus", "Refus d'une demande"),
    CONNEXION("Connexion", "Connexion au système"),
    DECONNEXION("Déconnexion", "Déconnexion du système"),
    CHANGEMENT_ROLE("Changement de rôle", "Modification des permissions"),
    GENERATION("Génération", "Génération d'un document"),
    EXPORT("Export", "Export de données"),
    ARCHIVAGE("Archivage", "Archivage d'une entité");

    private final String libelle;
    private final String description;

    TypeAction(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }
}

