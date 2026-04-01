package ma.fst.sgcd.model.enums;

public enum TypeDocument {
    RADIOGRAPHIE("Radiographie"),
    PHOTOGRAPHIE("Photographie"),
    COMPTE_RENDU("Compte rendu");

    private final String libelle;
    TypeDocument(String libelle) { this.libelle = libelle; }
    public String getLibelle() { return libelle; }
}
