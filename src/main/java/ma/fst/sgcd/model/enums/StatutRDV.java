package ma.fst.sgcd.model.enums;

public enum StatutRDV {
    PLANIFIE("Planifié", "secondary"),
    EN_SALLE_ATTENTE("En salle d'attente", "warning"),
    EN_COURS("En cours", "primary"),
    TERMINE("Terminé", "success"),
    ANNULE("Annulé", "danger"),
    NON_HONORE("Non honoré", "dark");

    private final String libelle;
    private final String badgeColor;
    StatutRDV(String libelle, String badgeColor) {
        this.libelle = libelle;
        this.badgeColor = badgeColor;
    }
    public String getLibelle() { return libelle; }
    public String getBadgeColor() { return badgeColor; }
}
