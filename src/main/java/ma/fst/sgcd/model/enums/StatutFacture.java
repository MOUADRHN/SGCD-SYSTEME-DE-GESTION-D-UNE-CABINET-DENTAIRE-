package ma.fst.sgcd.model.enums;

public enum StatutFacture {
    EN_ATTENTE("En attente", "warning"),
    PAYEE("Payée", "success"),
    ANNULEE("Annulée", "danger");

    private final String libelle;
    private final String badgeColor;
    StatutFacture(String libelle, String badgeColor) {
        this.libelle = libelle;
        this.badgeColor = badgeColor;
    }
    public String getLibelle() { return libelle; }
    public String getBadgeColor() { return badgeColor; }
}
