package ma.projet.rh.enums;

/**
 * Énumération des statuts d'une fiche de paie
 */
public enum StatutFichePaie {
    CALCULE("Calculé", "Fiche de paie calculée mais pas encore validée"),
    VALIDE("Validé", "Validé par les RH"),
    PAYE("Payé", "Paiement effectué");

    private final String libelle;
    private final String description;

    StatutFichePaie(String libelle, String description) {
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
     * Vérifie si la fiche peut être modifiée
     */
    public boolean isModifiable() {
        return this == CALCULE;
    }
}

