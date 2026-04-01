package ma.fst.sgcd.model.enums;

public enum Sexe {
    H("Homme"),
    F("Femme");

    private final String libelle;
    Sexe(String libelle) { this.libelle = libelle; }
    public String getLibelle() { return libelle; }
}
