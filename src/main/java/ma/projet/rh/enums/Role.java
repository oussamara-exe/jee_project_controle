package ma.projet.rh.enums;

/**
 * Énumération des rôles utilisateur dans le système
 * EMPLOYE : Employé standard avec accès limité à son propre profil
 * MANAGER : Gestionnaire avec accès aux employés de son équipe
 * RH : Ressources Humaines avec accès complet à la gestion RH
 * ADMIN : Administrateur système avec tous les droits
 */
public enum Role {
    EMPLOYE("Employé", "Accès employé standard"),
    MANAGER("Manager", "Gestionnaire d'équipe"),
    RH("Ressources Humaines", "Gestion RH complète"),
    ADMIN("Administrateur", "Administration système");

    private final String libelle;
    private final String description;

    Role(String libelle, String description) {
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
     * Vérifie si ce rôle a un niveau d'autorisation supérieur ou égal à un autre rôle
     */
    public boolean hasAuthorityOf(Role other) {
        return this.ordinal() >= other.ordinal();
    }
}

