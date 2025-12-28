package ma.projet.rh.enums;

/**
 * Énumération des statuts d'une feuille de temps
 */
public enum StatutFeuilleTemps {
    BROUILLON("Brouillon", "En cours de saisie"),
    SOUMIS("Soumis", "Soumis pour validation"),
    VALIDE("Validé", "Validé par le manager");

    private final String libelle;
    private final String description;

    StatutFeuilleTemps(String libelle, String description) {
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
     * Vérifie si la feuille de temps peut être modifiée
     */
    public boolean isModifiable() {
        return this == BROUILLON;
    }
}

