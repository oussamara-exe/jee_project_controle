package ma.projet.rh.enums;

/**
 * Énumération des types de congés disponibles
 */
public enum TypeConge {
    ANNUEL("Congé annuel", true, 22),
    MALADIE("Congé maladie", true, 90),
    MATERNITE("Congé maternité", false, 98),
    PATERNITE("Congé paternité", false, 3),
    SANS_SOLDE("Congé sans solde", false, 0),
    EXCEPTIONNEL("Congé exceptionnel", false, 0);

    private final String libelle;
    private final boolean deduireFromSolde;
    private final int joursMaxParAn;

    TypeConge(String libelle, boolean deduireFromSolde, int joursMaxParAn) {
        this.libelle = libelle;
        this.deduireFromSolde = deduireFromSolde;
        this.joursMaxParAn = joursMaxParAn;
    }

    public String getLibelle() {
        return libelle;
    }

    public boolean isDeduireFromSolde() {
        return deduireFromSolde;
    }

    public int getJoursMaxParAn() {
        return joursMaxParAn;
    }
}

