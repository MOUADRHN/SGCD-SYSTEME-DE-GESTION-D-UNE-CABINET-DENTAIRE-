package ma.fst.sgcd.model.enums;

public enum ModePaiement {
    ESPECES("Espèces"),
    CARTE_BANCAIRE("Carte bancaire"),
    CHEQUE("Chèque");

    private final String libelle;
    ModePaiement(String libelle) { this.libelle = libelle; }
    public String getLibelle() { return libelle; }
}
