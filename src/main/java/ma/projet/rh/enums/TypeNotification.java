package ma.projet.rh.enums;

/**
 * Énumération des types de notifications
 */
public enum TypeNotification {
    CONGE_A_VALIDER("Congé à valider", "info"),
    CONGE_APPROUVE("Congé approuvé", "success"),
    CONGE_REFUSE("Congé refusé", "warning"),
    PAIE_GENEREE("Paie générée", "info"),
    PAIE_VALIDEE("Paie validée", "success"),
    TEMPS_A_VALIDER("Feuille de temps à valider", "info"),
    TEMPS_VALIDEE("Feuille de temps validée", "success"),
    PROFIL_MODIFIE("Profil modifié", "info"),
    ALERTE_SYSTEME("Alerte système", "danger");

    private final String libelle;
    private final String cssClass;

    TypeNotification(String libelle, String cssClass) {
        this.libelle = libelle;
        this.cssClass = cssClass;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getCssClass() {
        return cssClass;
    }
}

