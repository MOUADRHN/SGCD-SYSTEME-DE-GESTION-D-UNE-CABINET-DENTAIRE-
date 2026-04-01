package ma.fst.sgcd.model.enums;

public enum Role {
    ADMINISTRATEUR("Administrateur"),
    DENTISTE("Dentiste"),
    ASSISTANTE("Assistante");

    private final String libelle;
    Role(String libelle) { this.libelle = libelle; }
    public String getLibelle() { return libelle; }
}
