package ma.fst.sgcd.model.enums;

public enum Statut {
    ACTIF("Actif"),
    INACTIF("Inactif");

    private final String libelle;
    Statut(String libelle) { this.libelle = libelle; }
    public String getLibelle() { return libelle; }
}
